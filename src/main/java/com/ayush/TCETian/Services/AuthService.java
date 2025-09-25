package com.ayush.TCETian.Services;

import com.ayush.TCETian.Entity.User;
import com.ayush.TCETian.payload.*;
import com.ayush.TCETian.Repositories.UserRepository;
import com.ayush.TCETian.Security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = jwtUtils.generateJwtToken(userDetails);

        return new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getName(),
                userDetails.getEmail(),
                userDetails.getAuthorities());
    }

    public MessageResponse registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new MessageResponse("Error: Email is already in use!");
        }

        // Create new user's account
        User user = User.builder()
                .name(signUpRequest.getName())
                .email(signUpRequest.getEmail())
                .password(encoder.encode(signUpRequest.getPassword()))
                .role(signUpRequest.getRole()) // STUDENT or ADMIN
                .verified(false) // initially false until email confirmed
                .verificationToken(UUID.randomUUID().toString()) // generate token for email verification
                .build();

        userRepository.save(user);

        // TODO: Send verification email here with verificationToken

        return new MessageResponse("User registered successfully! Please verify your email.");
    }

    public Optional<User> verifyUserByToken(String token) {
        return userRepository.findByVerificationToken(token);
    }

    public MessageResponse confirmUserVerification(String token) {
        Optional<User> userOpt = verifyUserByToken(token);

        if(userOpt.isEmpty()) {
            return new MessageResponse("Invalid verification token.");
        }

        User user = userOpt.get();
        user.setVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);

        return new MessageResponse("Email verified successfully!");
    }
}

