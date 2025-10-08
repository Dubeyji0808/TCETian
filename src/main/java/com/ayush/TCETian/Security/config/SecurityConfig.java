package com.ayush.TCETian.Security.config;

import com.ayush.TCETian.Security.jwt.AuthEntryPointJwt;
import com.ayush.TCETian.Security.jwt.AuthTokenFilter;
import com.ayush.TCETian.Services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public Auth APIs
                        .requestMatchers("/api/auth/signup", "/api/auth/signin", "/api/auth/refresh-token", "/api/auth/verify**").permitAll()

                        // Home page for both
                        .requestMatchers("/home").hasAnyRole("STUDENT", "ADMIN")

                        // ================= EVENTS =================
                        // Student can view and mark interest
                                // Publicly viewable events (no login required)
                                .requestMatchers(HttpMethod.GET, "/api/events/**").hasAnyRole("STUDENT", "ADMIN")

// Students can mark interest
                                .requestMatchers(HttpMethod.POST, "/api/events/*/interested").hasRole("STUDENT")

// Admin can manage events
                                .requestMatchers(HttpMethod.POST, "/api/events").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/events/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/events/**").hasRole("ADMIN")

                        // ================= POSTS =================
                        // Everyone logged in can read posts
                        .requestMatchers(HttpMethod.GET, "/api/posts/**").hasAnyRole("STUDENT", "ADMIN")

                        // Students can create, update own, like, comment
                        .requestMatchers(HttpMethod.POST, "/api/posts").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.PUT, "/api/posts/**").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.POST, "/api/posts/*/like").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.POST, "/api/posts/*/comments").hasRole("STUDENT")

                        // Admin can delete posts
                        .requestMatchers(HttpMethod.DELETE, "/api/posts/**").hasRole("ADMIN")

                        // ================= FALLBACK =================
                        .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000")); // React dev servers
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
