package com.docker.dto;

/**
 * DTO pour une photo de la galerie
 */
public class PhotoDTO {
    private Long id;
    private String url;
    private String thumbnailUrl;
    private String caption;
    private String category;
    private Integer displayOrder;
    private int likeCount;
    private int viewCount;
    private String title;
    private String description;
    private String tags;
    private String photographer;
    private String location;

    public PhotoDTO() {
    }

    public PhotoDTO(Long id, String url, String thumbnailUrl, String caption, String category, Integer displayOrder) {
        this.id = id;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
        this.caption = caption;
        this.category = category;
        this.displayOrder = displayOrder;
        this.likeCount = 0;
        this.viewCount = 0;
    }

    public PhotoDTO(Long id, String url, String thumbnailUrl, String caption, String category, Integer displayOrder, int likeCount) {
        this.id = id;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
        this.caption = caption;
        this.category = category;
        this.displayOrder = displayOrder;
        this.likeCount = likeCount;
        this.viewCount = 0;
    }

    public PhotoDTO(Long id, String url, String thumbnailUrl, String caption, String category, Integer displayOrder, int likeCount, int viewCount) {
        this.id = id;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
        this.caption = caption;
        this.category = category;
        this.displayOrder = displayOrder;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getPhotographer() {
        return photographer;
    }

    public void setPhotographer(String photographer) {
        this.photographer = photographer;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
