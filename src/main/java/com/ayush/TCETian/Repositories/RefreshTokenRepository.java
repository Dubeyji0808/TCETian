package com.ayush.TCETian.Repositories;

import com.ayush.TCETian.Entity.RefreshToken;
import com.ayush.TCETian.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    int deleteByUser(User user);
}

