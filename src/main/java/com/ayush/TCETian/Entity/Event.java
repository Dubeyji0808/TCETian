package com.ayush.TCETian.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private String category;

    @ManyToOne
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;

    private String coverImageUrl;

    @Column(nullable = false)
    private LocalDateTime timeStart;

    @Column(nullable = false)
    private LocalDateTime timeEnd;

    @Enumerated(EnumType.STRING)
    private EventStatus status; // UPCOMING, ONGOING, COMPLETED

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<EventInterest> interestedUsers;
}
