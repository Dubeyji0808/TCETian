package com.ayush.TCETian.Repositories;

import com.ayush.TCETian.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username); // ✅ Added for username-based authentication

    Boolean existsByEmail(String email);
    Optional<User> findByVerificationToken(String token);
}