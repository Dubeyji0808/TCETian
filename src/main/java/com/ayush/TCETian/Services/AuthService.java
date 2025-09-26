package com.ayush.TCETian.Services;

import com.ayush.TCETian.Entity.RefreshToken;
import com.ayush.TCETian.Entity.User;
import com.ayush.TCETian.Repositories.UserRepository;
import com.ayush.TCETian.Security.jwt.JwtUtils;
import com.ayush.TCETian.payload.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;
    private final RefreshTokenService refreshTokenService; // <-- added

    /**
     * Authenticate user and return JWT + refresh token in response
     */
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        // 1. Check if user exists
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + loginRequest.getEmail()));

        // 2. Check if verified
        if (!user.isVerified()) {
            throw new DisabledException("User is not verified. Please check your email.");
        }

        // 3. Proceed with authentication
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = jwtUtils.generateJwtToken(userDetails);

        // Generate refresh token (expects createRefreshToken to return a RefreshToken entity)
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return new JwtResponse(
                jwt,
                refreshToken.getToken(),      // refresh token string
                userDetails.getId(),
                userDetails.getName(),
                userDetails.getEmail(),
                userDetails.getAuthorities()
        );
    }

    /**
     * Register new user and send email verification link
     */
    public MessageResponse registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new MessageResponse("Error: Email is already in use!");
        }

        User user = User.builder()
                .name(signUpRequest.getName())
                .email(signUpRequest.getEmail())
                .password(encoder.encode(signUpRequest.getPassword()))
                .role(signUpRequest.getRole()) // STUDENT or ADMIN
                .verified(false)
                .verificationToken(UUID.randomUUID().toString())
                .build();

        userRepository.save(user);

        try {
            // Try sending verification email
            emailService.sendVerificationEmail(user.getEmail(), user.getVerificationToken());
        } catch (Exception e) {
            e.printStackTrace(); // log stacktrace
            // rollback user to avoid unverified entries
            userRepository.delete(user);
            return new MessageResponse("Registration failed: Unable to send verification email. Please try again.");
        }

        return new MessageResponse("User registered successfully! Please verify your email.");
    }

    /**
     * Verify user by token and activate account
     */
    public boolean verifyUser(String token) {
        Optional<User> userOpt = userRepository.findByVerificationToken(token);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setVerified(true);
            user.setVerificationToken(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }
}
