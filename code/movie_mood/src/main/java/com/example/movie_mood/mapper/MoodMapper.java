package com.example.movie_mood.mapper;

import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.dto.response.MoodResponse;
import org.springframework.stereotype.Component;

@Component
public class MoodMapper {

    public MoodResponse toResponse(Mood mood) {
        return new MoodResponse(
                mood.name(),
                toDisplayName(mood)
        );
    }

    private String toDisplayName(Mood mood) {
        String name = mood.name().toLowerCase();

        return Character.toUpperCase(name.charAt(0))
                + name.substring(1);
    }
}