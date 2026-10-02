package com.example.movie_mood.mapper;

import com.example.movie_mood.domain.enums.Mood;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MoodGenreMapper {

    public List<Integer> getGenreIds(Mood mood) {
        return switch (mood) {
            case HAPPY -> List.of(35, 16, 10751);
            case SAD -> List.of(18);
            case EXCITED -> List.of(28, 12, 53);
            case RELAXED -> List.of(10751, 16, 35);
            case ROMANTIC -> List.of(10749);
            case SCARY -> List.of(27, 53);
        };
    }
}