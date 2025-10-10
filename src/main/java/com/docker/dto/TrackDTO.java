package com.docker.dto;

/**
 * DTO pour un morceau de musique
 */
public class TrackDTO {
    private Long id;
    private String title;
    private Integer trackNumber;
    private String duration;
    private String audioUrl;
    private String spotifyUrl;

    public TrackDTO() {
    }

    public TrackDTO(Long id, String title, Integer trackNumber, String duration, String audioUrl, String spotifyUrl) {
        this.id = id;
        this.title = title;
        this.trackNumber = trackNumber;
        this.duration = duration;
        this.audioUrl = audioUrl;
        this.spotifyUrl = spotifyUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(Integer trackNumber) {
        this.trackNumber = trackNumber;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getSpotifyUrl() {
        return spotifyUrl;
    }

    public void setSpotifyUrl(String spotifyUrl) {
        this.spotifyUrl = spotifyUrl;
    }
}
