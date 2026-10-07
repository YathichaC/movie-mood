package com.example.movie_mood.service;

import org.springframework.web.multipart.MultipartFile;

public interface PlaylistImageService {

    String saveImage(MultipartFile file);
}