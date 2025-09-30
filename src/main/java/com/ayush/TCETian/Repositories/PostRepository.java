package com.ayush.TCETian.Repositories;

import com.ayush.TCETian.Entity.Post;
import com.ayush.TCETian.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // Find posts by author
    List<Post> findByAuthor(User author);

    // Search posts by title (case insensitive)
    List<Post> findByTitleContainingIgnoreCase(String keyword);

    // Find posts ordered by creation time (latest first)
    List<Post> findAllByOrderByCreatedAtDesc();
}
