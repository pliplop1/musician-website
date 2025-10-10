package com.docker.dto;

import java.util.List;

/**
 * DTO pour un album
 */
public class AlbumDTO {
    private Long id;
    private String title;
    private String coverUrl;
    private Integer year;
    private String description;
    private List<TrackDTO> tracks;
    private String spotifyUrl;
    private String appleMusicUrl;

    public AlbumDTO() {
    }

    public AlbumDTO(Long id, String title, String coverUrl, Integer year, String description,
                    List<TrackDTO> tracks, String spotifyUrl, String appleMusicUrl) {
        this.id = id;
        this.title = title;
        this.coverUrl = coverUrl;
        this.year = year;
        this.description = description;
        this.tracks = tracks;
        this.spotifyUrl = spotifyUrl;
        this.appleMusicUrl = appleMusicUrl;
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

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<TrackDTO> getTracks() {
        return tracks;
    }

    public void setTracks(List<TrackDTO> tracks) {
        this.tracks = tracks;
    }

    public String getSpotifyUrl() {
        return spotifyUrl;
    }

    public void setSpotifyUrl(String spotifyUrl) {
        this.spotifyUrl = spotifyUrl;
    }

    public String getAppleMusicUrl() {
        return appleMusicUrl;
    }

    public void setAppleMusicUrl(String appleMusicUrl) {
        this.appleMusicUrl = appleMusicUrl;
    }
}
