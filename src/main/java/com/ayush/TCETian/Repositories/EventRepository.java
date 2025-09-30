package com.ayush.TCETian.Repositories;

import com.ayush.TCETian.Entity.Event;
import com.ayush.TCETian.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Find all events organized by a specific user
    List<Event> findByOrganizer(User organizer);

    // Find upcoming events
    List<Event> findByTimeStartAfter(LocalDateTime now);

    // Find past events
    List<Event> findByTimeEndBefore(LocalDateTime now);

    // Find ongoing events
    List<Event> findByTimeStartBeforeAndTimeEndAfter(LocalDateTime start, LocalDateTime end);
}
