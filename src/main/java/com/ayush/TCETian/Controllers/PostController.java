package com.ayush.TCETian.Controllers;

import com.ayush.TCETian.Entity.Comment;
import com.ayush.TCETian.Services.PostService;
import com.ayush.TCETian.dto.PostRequest;
import com.ayush.TCETian.dto.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostRequest request, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postService.mapToResponse(postService.createPost(request, principal.getName())));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long postId,
                                                   @RequestBody PostRequest request,
                                                   Principal principal) {
        return ResponseEntity.ok(postService.mapToResponse(postService.updatePost(postId, request, principal.getName())));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId, Principal principal) {
        postService.deletePost(postId, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        return ResponseEntity.ok(postService.mapToResponse(postService.getAllPosts()));
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<PostResponse> likePost(@PathVariable Long postId, Principal principal) {
        return ResponseEntity.ok(postService.mapToResponse(postService.likePost(postId, principal.getName())));
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<Comment> addComment(@PathVariable Long postId,
                                              @RequestBody Map<String, String> payload,
                                              Principal principal) {
        String content = payload.get("content");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postService.addComment(postId, principal.getName(), content));
    }

    @GetMapping("/my")
    public ResponseEntity<List<PostResponse>> getMyPosts(Principal principal) {
        return ResponseEntity.ok(postService.mapToResponse(postService.getPostsByAuthor(principal.getName())));
    }

    @GetMapping("/search")
    public ResponseEntity<List<PostResponse>> searchPosts(@RequestParam("q") String keyword) {
        return ResponseEntity.ok(postService.mapToResponse(postService.searchPostsByTitle(keyword)));
    }

    @GetMapping("/latest")
    public ResponseEntity<List<PostResponse>> getLatestPosts() {
        return ResponseEntity.ok(postService.mapToResponse(postService.getPostsOrderedByDate()));
    }
}
