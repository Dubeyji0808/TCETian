package com.ayush.TCETian.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostRequest {
    private String title;
    private String content;
    private String coverImageUrl;
}
