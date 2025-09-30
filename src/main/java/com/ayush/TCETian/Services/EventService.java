package com.ayush.TCETian.Services;

import com.ayush.TCETian.Entity.Event;
import com.ayush.TCETian.Entity.User;
import com.ayush.TCETian.Repositories.EventRepository;
import com.ayush.TCETian.Repositories.UserRepository;
import com.ayush.TCETian.dto.EventRequest;
import com.ayush.TCETian.dto.EventResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public Event createEvent(EventRequest request, String organizerEmail) {
        User organizer = userRepository.findByEmail(organizerEmail)
                .orElseThrow(() -> new EntityNotFoundException("Organizer not found"));

        if (request.getTimeEnd().isBefore(request.getTimeStart())) {
            throw new IllegalArgumentException("Event end time cannot be before start time");
        }

        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .coverImageUrl(request.getCoverImageUrl())
                .timeStart(request.getTimeStart())
                .timeEnd(request.getTimeEnd())
                .organizer(organizer)
                .build();

        event.updateStatus();

        return eventRepository.save(event);
    }

    public Event updateEvent(Long eventId, EventRequest request, String organizerEmail) {
        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        if (!existingEvent.getOrganizer().getEmail().equals(organizerEmail)) {
            throw new AccessDeniedException("You are not the organizer of this event");
        }

        if (request.getTimeEnd().isBefore(request.getTimeStart())) {
            throw new IllegalArgumentException("Event end time cannot be before start time");
        }

        existingEvent.setTitle(request.getTitle());
        existingEvent.setDescription(request.getDescription());
        existingEvent.setCategory(request.getCategory());
        existingEvent.setCoverImageUrl(request.getCoverImageUrl());
        existingEvent.setTimeStart(request.getTimeStart());
        existingEvent.setTimeEnd(request.getTimeEnd());
        existingEvent.updateStatus();

        return eventRepository.save(existingEvent);
    }

    public void deleteEvent(Long eventId, String organizerEmail) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        if (!event.getOrganizer().getEmail().equals(organizerEmail)) {
            throw new AccessDeniedException("You are not the organizer of this event");
        }

        eventRepository.delete(event);
    }

    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public void markUserInterested(Long eventId, String userEmail) {
        Event event = getEventById(eventId);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        event.getInterestedUsers().add(user);
        eventRepository.save(event);
    }

    public List<Event> getEventsByOrganizer(String email) {
        User organizer = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Organizer not found"));
        return eventRepository.findByOrganizer(organizer);
    }

    public List<Event> getUpcomingEvents() {
        return eventRepository.findByTimeStartAfter(LocalDateTime.now());
    }

    public List<Event> getPastEvents() {
        return eventRepository.findByTimeEndBefore(LocalDateTime.now());
    }

    public List<Event> getOngoingEvents() {
        LocalDateTime now = LocalDateTime.now();
        return eventRepository.findByTimeStartBeforeAndTimeEndAfter(now, now);
    }

    // ✅ Mapping methods
    public EventResponse mapToResponse(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .category(event.getCategory())
                .coverImageUrl(event.getCoverImageUrl())
                .timeStart(event.getTimeStart())
                .timeEnd(event.getTimeEnd())
                .status(event.getStatus().name())
                .organizerEmail(event.getOrganizer().getEmail())
                .build();
    }

    public List<EventResponse> mapToResponse(List<Event> events) {
        return events.stream().map(this::mapToResponse).collect(Collectors.toList());
    }
}
