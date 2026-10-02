package com.example.movie_mood.mapper;

import com.example.movie_mood.domain.enums.Mood;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MoodGenreMapper {

    public List<Integer> getGenreIds(Mood mood) {
        return switch (mood) {

            case HAPPY -> List.of(
                    35,     // Comedy
                    16,     // Animation
                    10751,  // Family
                    10402,  // Music
                    14      // Fantasy
            );

            case SAD -> List.of(
                    18,     // Drama
                    36,     // History
                    10752,  // War
                    99      // Documentary
            );

            case EXCITED -> List.of(
                    28,     // Action
                    12,     // Adventure
                    53,     // Thriller
                    878,    // Science Fiction
                    80,     // Crime
                    37      // Western
            );

            case RELAXED -> List.of(
                    10751,  // Family
                    16,     // Animation
                    35,     // Comedy
                    99,     // Documentary
                    10402,  // Music
                    10770   // TV Movie
            );

            case ROMANTIC -> List.of(
                    10749,  // Romance
                    18,     // Drama
                    35      // Comedy
            );

            case SCARY -> List.of(
                    27,     // Horror
                    53,     // Thriller
                    9648,   // Mystery
                    80      // Crime
            );
        };
    }
}