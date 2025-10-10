package com.docker.dto;

import java.util.List;

/**
 * DTO pour la biographie de l'artiste
 */
public class BiographyDTO {
    private String content;
    private List<TimelineEventDTO> timeline;
    private List<String> photoUrls;

    public BiographyDTO() {
    }

    public BiographyDTO(String content, List<TimelineEventDTO> timeline, List<String> photoUrls) {
        this.content = content;
        this.timeline = timeline;
        this.photoUrls = photoUrls;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<TimelineEventDTO> getTimeline() {
        return timeline;
    }

    public void setTimeline(List<TimelineEventDTO> timeline) {
        this.timeline = timeline;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }

    public static class TimelineEventDTO {
        private Integer year;
        private String title;
        private String description;
        private String photoUrl;

        public TimelineEventDTO() {
        }

        public TimelineEventDTO(Integer year, String title, String description, String photoUrl) {
            this.year = year;
            this.title = title;
            this.description = description;
            this.photoUrl = photoUrl;
        }

        public Integer getYear() {
            return year;
        }

        public void setYear(Integer year) {
            this.year = year;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }

        public void setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
        }
    }
}
