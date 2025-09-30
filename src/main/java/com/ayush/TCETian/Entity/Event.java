package com.ayush.TCETian.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

    private String category;

    private String coverImageUrl;

    @Column(nullable = false)
    private LocalDateTime timeStart;

    @Column(nullable = false)
    private LocalDateTime timeEnd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    @ManyToOne
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;

    public void updateStatus() {
        LocalDateTime now = LocalDateTime.now();
        if (timeEnd.isBefore(now)) {
            status = EventStatus.COMPLETED;
        } else if (timeStart.isAfter(now)) {
            status = EventStatus.UPCOMING;
        } else {
            status = EventStatus.ONGOING;
        }
    }

    @ManyToMany
    @JoinTable(
            name = "event_interests",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> interestedUsers = new HashSet<>();
}
