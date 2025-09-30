package com.ayush.TCETian.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
    private Long id;
    private String content;
    private String authorEmail;
    private LocalDateTime createdAt;
}
