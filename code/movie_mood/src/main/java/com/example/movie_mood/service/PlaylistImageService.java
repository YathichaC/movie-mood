package com.example.movie_mood.service;

import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

public interface PlaylistImageService {

     String saveImage(
            MultipartFile file,
            UUID userId,
            UUID playlistId);

    void deleteImage(String imageUrl);
}