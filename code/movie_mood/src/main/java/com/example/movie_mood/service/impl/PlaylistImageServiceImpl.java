package com.example.movie_mood.service.impl;

import com.example.movie_mood.service.PlaylistImageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class PlaylistImageServiceImpl implements PlaylistImageService {

    private static final Path UPLOAD_DIRECTORY =
            Paths.get("uploads", "playlists");

    @Override
    public String saveImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return null;
        }

        String contentType = file.getContentType();

        if (contentType == null
                || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException(
                    "Cover image must be an image file");
        }

        try {
            Files.createDirectories(UPLOAD_DIRECTORY);

            String originalFilename = file.getOriginalFilename();
            String extension = getExtension(originalFilename);

            String filename =
                    UUID.randomUUID() + extension;

            Path destination =
                    UPLOAD_DIRECTORY.resolve(filename);

            Files.copy(
                    file.getInputStream(),
                    destination,
                    StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/playlists/" + filename;

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Failed to save playlist cover image",
                    exception);
        }
    }

    private String getExtension(String filename) {

        if (filename == null) {
            return "";
        }

        int dotIndex = filename.lastIndexOf('.');

        if (dotIndex < 0) {
            return "";
        }

        return filename.substring(dotIndex);
    }
}