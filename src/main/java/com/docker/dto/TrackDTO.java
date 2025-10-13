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
    private int likeCount; // Nombre de likes
    private int playCount; // Nombre d'écoutes

    public TrackDTO() {
    }

    public TrackDTO(Long id, String title, Integer trackNumber, String duration, String audioUrl, String spotifyUrl) {
        this.id = id;
        this.title = title;
        this.trackNumber = trackNumber;
        this.duration = duration;
        this.audioUrl = audioUrl;
        this.spotifyUrl = spotifyUrl;
        this.likeCount = 0;
        this.playCount = 0;
    }

    public TrackDTO(Long id, String title, Integer trackNumber, String duration, String audioUrl, String spotifyUrl, int likeCount) {
        this.id = id;
        this.title = title;
        this.trackNumber = trackNumber;
        this.duration = duration;
        this.audioUrl = audioUrl;
        this.spotifyUrl = spotifyUrl;
        this.likeCount = likeCount;
        this.playCount = 0;
    }

    public TrackDTO(Long id, String title, Integer trackNumber, String duration, String audioUrl, String spotifyUrl, int likeCount, int playCount) {
        this.id = id;
        this.title = title;
        this.trackNumber = trackNumber;
        this.duration = duration;
        this.audioUrl = audioUrl;
        this.spotifyUrl = spotifyUrl;
        this.likeCount = likeCount;
        this.playCount = playCount;
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

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }
}
