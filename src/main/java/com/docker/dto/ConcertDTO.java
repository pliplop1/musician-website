package com.docker.dto;

import java.time.LocalDateTime;

/**
 * DTO pour un concert
 */
public class ConcertDTO {
    private Long id;
    private String location;
    private String venue;
    private LocalDateTime date;
    private String description;
    private String ticketUrl;
    private String photoUrl;
    private boolean isPast;
    private Long daysUntil;

    public ConcertDTO() {
    }

    public ConcertDTO(Long id, String location, String venue, LocalDateTime date, String description,
                      String ticketUrl, String photoUrl, boolean isPast, Long daysUntil) {
        this.id = id;
        this.location = location;
        this.venue = venue;
        this.date = date;
        this.description = description;
        this.ticketUrl = ticketUrl;
        this.photoUrl = photoUrl;
        this.isPast = isPast;
        this.daysUntil = daysUntil;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTicketUrl() {
        return ticketUrl;
    }

    public void setTicketUrl(String ticketUrl) {
        this.ticketUrl = ticketUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public boolean isPast() {
        return isPast;
    }

    public void setPast(boolean past) {
        isPast = past;
    }

    public Long getDaysUntil() {
        return daysUntil;
    }

    public void setDaysUntil(Long daysUntil) {
        this.daysUntil = daysUntil;
    }
}
