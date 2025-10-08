package com.ayush.TCETian.Services;

import com.ayush.TCETian.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private Long id;
    private String username; // actual display username (e.g., "Batman")
    private String email;    // login identifier
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    // Convenience - return the display name
    public String getDisplayName() {
        return username;
    }

    public static UserDetailsImpl build(User user) {
        // Map Role enum set to GrantedAuthority (ROLE_STUDENT etc.)
        Collection<GrantedAuthority> authorities = Optional.ofNullable(user.getRoles())
                .orElse(Set.of())
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    // IMPORTANT: Spring uses getUsername() to identify the principal used in authentication.
    // We want login/authentication to be done using email, so return email here.
    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    // Other UserDetails methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true; // you can change to user.isVerified() checks externally if needed
    }

    // For convenience in AuthService or Jwt creation
    public Long getId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }
}