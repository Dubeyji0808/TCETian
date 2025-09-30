package com.ayush.TCETian.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponse {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String coverImageUrl;
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;
    private String status;
    private String organizerEmail;
}
