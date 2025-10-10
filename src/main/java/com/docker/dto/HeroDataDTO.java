package com.docker.dto;

/**
 * DTO pour les données de la Hero Section
 * Contient les informations principales affichées en haut du site
 */
public class HeroDataDTO {
    private String artistName;
    private String tagline;
    private String videoUrl;
    private String posterImageUrl;
    private LatestReleaseDTO latestRelease;

    public HeroDataDTO() {
    }

    public HeroDataDTO(String artistName, String tagline, String videoUrl, String posterImageUrl, LatestReleaseDTO latestRelease) {
        this.artistName = artistName;
        this.tagline = tagline;
        this.videoUrl = videoUrl;
        this.posterImageUrl = posterImageUrl;
        this.latestRelease = latestRelease;
    }

    // Getters and Setters
    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getPosterImageUrl() {
        return posterImageUrl;
    }

    public void setPosterImageUrl(String posterImageUrl) {
        this.posterImageUrl = posterImageUrl;
    }

    public LatestReleaseDTO getLatestRelease() {
        return latestRelease;
    }

    public void setLatestRelease(LatestReleaseDTO latestRelease) {
        this.latestRelease = latestRelease;
    }

    // Nested DTO pour la dernière sortie
    public static class LatestReleaseDTO {
        private String title;
        private String coverUrl;
        private String spotifyUrl;
        private String appleMusicUrl;

        public LatestReleaseDTO() {
        }

        public LatestReleaseDTO(String title, String coverUrl, String spotifyUrl, String appleMusicUrl) {
            this.title = title;
            this.coverUrl = coverUrl;
            this.spotifyUrl = spotifyUrl;
            this.appleMusicUrl = appleMusicUrl;
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
}
