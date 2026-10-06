package com.example.movie_mood.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Mood information")
public class MoodResponse {

    @Schema(example = "HAPPY")
    private String name;

    @Schema(example = "Happy")
    private String displayName;

    public MoodResponse() {
    }

    public MoodResponse(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}