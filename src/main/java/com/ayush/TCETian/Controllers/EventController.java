package com.ayush.TCETian.Controllers;

import com.ayush.TCETian.Entity.Event;
import com.ayush.TCETian.Services.EventService;
import com.ayush.TCETian.dto.EventRequest;
import com.ayush.TCETian.dto.EventResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@RequestBody EventRequest request, Principal principal) {
        Event createdEvent = eventService.createEvent(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.mapToResponse(createdEvent));
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable Long eventId,
                                                     @RequestBody EventRequest request,
                                                     Principal principal) {
        Event updatedEvent = eventService.updateEvent(eventId, request, principal.getName());
        return ResponseEntity.ok(eventService.mapToResponse(updatedEvent));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId, Principal principal) {
        eventService.deleteEvent(eventId, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        List<Event> events = eventService.getAllEvents();
        return ResponseEntity.ok(eventService.mapToResponse(events));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Long eventId) {
        Event event = eventService.getEventById(eventId);
        return ResponseEntity.ok(eventService.mapToResponse(event));
    }

    @PostMapping("/{eventId}/interested")
    public ResponseEntity<String> markUserInterested(@PathVariable Long eventId, Principal principal) {
        eventService.markUserInterested(eventId, principal.getName());
        return ResponseEntity.ok("Marked as interested");
    }

    @GetMapping("/organizer")
    public ResponseEntity<List<EventResponse>> getEventsByOrganizer(Principal principal) {
        List<Event> events = eventService.getEventsByOrganizer(principal.getName());
        return ResponseEntity.ok(eventService.mapToResponse(events));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<EventResponse>> getUpcomingEvents() {
        List<Event> events = eventService.getUpcomingEvents();
        return ResponseEntity.ok(eventService.mapToResponse(events));
    }

    @GetMapping("/past")
    public ResponseEntity<List<EventResponse>> getPastEvents() {
        List<Event> events = eventService.getPastEvents();
        return ResponseEntity.ok(eventService.mapToResponse(events));
    }

    @GetMapping("/ongoing")
    public ResponseEntity<List<EventResponse>> getOngoingEvents() {
        List<Event> events = eventService.getOngoingEvents();
        return ResponseEntity.ok(eventService.mapToResponse(events));
    }
}
