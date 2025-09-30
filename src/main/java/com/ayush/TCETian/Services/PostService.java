package com.ayush.TCETian.Services;

import com.ayush.TCETian.Entity.*;
import com.ayush.TCETian.Repositories.*;
import com.ayush.TCETian.dto.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public Post createPost(PostRequest request, String authorEmail) {
        User author = userRepository.findByEmail(authorEmail)
                .orElseThrow(() -> new EntityNotFoundException("Author not found"));

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .coverImageUrl(request.getCoverImageUrl())
                .author(author)
                .createdAt(LocalDateTime.now())
                .build();

        return postRepository.save(post);
    }

    public Post updatePost(Long postId, PostRequest request, String authorEmail) {
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        if (!existingPost.getAuthor().getEmail().equals(authorEmail)) {
            throw new SecurityException("You are not the author of this post");
        }

        existingPost.setTitle(request.getTitle());
        existingPost.setContent(request.getContent());
        existingPost.setCoverImageUrl(request.getCoverImageUrl());

        return postRepository.save(existingPost);
    }

    public void deletePost(Long postId, String requesterEmail) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        boolean isAuthor = post.getAuthor().getEmail().equals(requesterEmail);
        boolean isAdmin = requester.isAdmin(); // ✅ use helper

        if (!isAuthor && !isAdmin) {
            throw new SecurityException("You are not allowed to delete this post");
        }

        postRepository.delete(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post likePost(Long postId, String userEmail) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        post.getLikedBy().add(user);
        return postRepository.save(post);
    }

    public Comment addComment(Long postId, String userEmail, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        User author = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Comment comment = Comment.builder()
                .post(post)
                .author(author)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();

        post.getComments().add(comment);
        postRepository.save(post);

        return comment;
    }

    public List<Post> getPostsByAuthor(String email) {
        User author = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Author not found"));
        return postRepository.findByAuthor(author);
    }

    public List<Post> searchPostsByTitle(String keyword) {
        return postRepository.findByTitleContainingIgnoreCase(keyword);
    }

    public List<Post> getPostsOrderedByDate() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public PostResponse mapToResponse(Post post) {
        List<CommentResponse> comments = post.getComments().stream()
                .map(c -> CommentResponse.builder()
                        .id(c.getId())
                        .content(c.getContent())
                        .authorEmail(c.getAuthor().getEmail())
                        .createdAt(c.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        List<String> likedByEmails = post.getLikedBy().stream()
                .map(User::getEmail)
                .collect(Collectors.toList());

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .coverImageUrl(post.getCoverImageUrl())
                .createdAt(post.getCreatedAt())
                .authorEmail(post.getAuthor().getEmail())
                .comments(comments)
                .likedByEmails(likedByEmails)
                .build();
    }

    public List<PostResponse> mapToResponse(List<Post> posts) {
        return posts.stream().map(this::mapToResponse).collect(Collectors.toList());
    }
}
