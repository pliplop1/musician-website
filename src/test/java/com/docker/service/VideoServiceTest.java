package com.docker.service;

import com.docker.entity.Video;
import com.docker.entity.VideoType;
import com.docker.repository.VideoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour VideoService
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class VideoServiceTest {

    @Autowired
    private VideoService videoService;

    @Autowired
    private VideoRepository videoRepository;

    @Test
    void testGetAllVideos_ReturnsAllVideos() {
        // Given
        createAndSaveEmbedVideo("Video 1", "<iframe>youtube1</iframe>");
        createAndSaveEmbedVideo("Video 2", "<iframe>youtube2</iframe>");

        // When
        List<Video> videos = videoService.getAllVideos();

        // Then
        assertTrue(videos.size() >= 2);
    }

    @Test
    void testSaveEmbedVideo_Success() {
        // Given
        String title = "Test Video";
        String embedCode = "<iframe src='youtube'></iframe>";

        // When
        videoService.saveEmbedVideo(title, embedCode);

        // Then
        List<Video> videos = videoRepository.findAll();
        Video savedVideo = videos.stream()
                .filter(v -> v.getTitle().equals(title))
                .findFirst()
                .orElse(null);

        assertNotNull(savedVideo);
        assertEquals(title, savedVideo.getTitle());
        assertEquals(embedCode, savedVideo.getEmbedCode());
        assertEquals(VideoType.EMBED, savedVideo.getVideoType());
    }

    @Test
    void testDeleteVideo_ExistingEmbedVideo_Success() throws Exception {
        // Given
        Video video = createAndSaveEmbedVideo("Delete Me", "<iframe>test</iframe>");

        // When
        videoService.deleteVideo(video.getId());

        // Then
        assertFalse(videoRepository.findById(video.getId()).isPresent());
    }

    @Test
    void testDeleteVideo_NonExistingVideo_ThrowsException() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> videoService.deleteVideo(999L));
    }

    /**
     * Helper method to create and save an embed video
     */
    private Video createAndSaveEmbedVideo(String title, String embedCode) {
        videoService.saveEmbedVideo(title, embedCode);
        return videoRepository.findAll().stream()
                .filter(v -> v.getTitle().equals(title))
                .findFirst()
                .orElseThrow();
    }
}
