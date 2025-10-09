package com.docker.controller.api;

import com.docker.entity.Track;
import com.docker.entity.Video;
import com.docker.service.TrackService;
import com.docker.service.VideoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final TrackService trackService;
    private final VideoService videoService;

    public ApiController(TrackService trackService, VideoService videoService) {
        this.trackService = trackService;
        this.videoService = videoService;
    }

    @GetMapping("/tracks")
    public ResponseEntity<List<Track>> getAllTracks() {
        List<Track> tracks = trackService.getAllTracks();
        return ResponseEntity.ok(tracks);
    }

    @GetMapping("/videos")
    public ResponseEntity<List<Video>> getAllVideos() {
        List<Video> videos = videoService.getAllVideos();
        return ResponseEntity.ok(videos);
    }
}