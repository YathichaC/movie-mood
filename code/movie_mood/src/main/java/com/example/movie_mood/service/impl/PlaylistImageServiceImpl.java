package com.example.movie_mood.service.impl;

import com.example.movie_mood.service.PlaylistImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Service
public class PlaylistImageServiceImpl implements PlaylistImageService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp");

    private final RestClient restClient;
    private final String supabaseUrl;
    private final String bucket;

    public PlaylistImageServiceImpl(
            @Value("${supabase.url}") String supabaseUrl,
            @Value("${supabase.secret-key}") String secretKey,
            @Value("${supabase.storage.bucket}") String bucket) {

        this.supabaseUrl = removeTrailingSlash(supabaseUrl);
        this.bucket = bucket;

        this.restClient = RestClient.builder()
                .baseUrl(this.supabaseUrl)
                .defaultHeader("apikey", secretKey)
                .build();
    }

    @Override
    public String saveImage(
            MultipartFile file,
            UUID userId,
            UUID playlistId) {

        if (file == null || file.isEmpty()) {
            return null;
        }

        validateImage(file);

        String extension = getExtension(
                file.getOriginalFilename(),
                file.getContentType());

        String objectPath = userId
                + "/"
                + playlistId
                + "/"
                + UUID.randomUUID()
                + extension;

        try {
            restClient.post()
                    .uri("/storage/v1/object/{bucket}/{path}",
                            bucket,
                            objectPath)
                    .contentType(MediaType.parseMediaType(
                            file.getContentType()))
                    .body(file.getBytes())
                    .retrieve()
                    .toBodilessEntity();

            return supabaseUrl
                    + "/storage/v1/object/public/"
                    + bucket
                    + "/"
                    + objectPath;

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Failed to read playlist cover image",
                    exception);
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Failed to upload playlist cover image: "
                            + exception.getMessage(),
                    exception);
        }
    }

    @Override
    public void deleteImage(String imageUrl) {

        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        String publicPrefix = supabaseUrl
                + "/storage/v1/object/public/"
                + bucket
                + "/";

        if (!imageUrl.startsWith(publicPrefix)) {
            throw new IllegalArgumentException(
                    "Invalid playlist image URL");
        }

        String objectPath = imageUrl.substring(publicPrefix.length());

        try {
            restClient.delete()
                    .uri("/storage/v1/object/{bucket}/{path}",
                            bucket,
                            objectPath)
                    .retrieve()
                    .toBodilessEntity();

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Failed to delete playlist image",
                    exception);
        }
    }

    private void validateImage(MultipartFile file) {

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "Cover image must not exceed 5 MB");
        }

        String contentType = file.getContentType();

        if (contentType == null
                || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException(
                    "Cover image must be JPEG, PNG, or WEBP");
        }
    }

    private String getExtension(
            String originalFilename,
            String contentType) {

        if (originalFilename != null) {
            int dotIndex = originalFilename.lastIndexOf('.');

            if (dotIndex >= 0) {
                String extension = originalFilename
                        .substring(dotIndex)
                        .toLowerCase();

                if (Set.of(
                        ".jpg",
                        ".jpeg",
                        ".png",
                        ".webp").contains(extension)) {
                    return extension;
                }
            }
        }

        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
    }

    private static String removeTrailingSlash(String value) {

        if (value.endsWith("/")) {
            return value.substring(0, value.length() - 1);
        }

        return value;
    }
}