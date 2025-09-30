package com.ayush.TCETian.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private String coverImageUrl;
    private String authorEmail;
    private int likesCount;
    private LocalDateTime createdAt;
    private List<CommentResponse> comments;
    private List<String> likedByEmails;
}
