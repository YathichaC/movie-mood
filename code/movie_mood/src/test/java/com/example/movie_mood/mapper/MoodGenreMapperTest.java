package com.example.movie_mood.mapper;

import com.example.movie_mood.domain.enums.Mood;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MoodGenreMapperTest {

    private MoodGenreMapper moodGenreMapper;

    @BeforeEach
    void setUp() {
        moodGenreMapper = new MoodGenreMapper();
    }

    @Test
    void shouldMapHappyMoodToGenres() {
        assertEquals(
                List.of(35, 16, 10751, 10402, 14),
                moodGenreMapper.getGenreIds(Mood.HAPPY)
        );
    }

    @Test
    void shouldMapSadMoodToGenres() {
        assertEquals(
                List.of(18, 36, 10752, 99),
                moodGenreMapper.getGenreIds(Mood.SAD)
        );
    }

    @Test
    void shouldMapExcitedMoodToGenres() {
        assertEquals(
                List.of(28, 12, 53, 878, 80, 37),
                moodGenreMapper.getGenreIds(Mood.EXCITED)
        );
    }

    @Test
    void shouldMapRelaxedMoodToGenres() {
        assertEquals(
                List.of(10751, 16, 35, 99, 10402, 10770),
                moodGenreMapper.getGenreIds(Mood.RELAXED)
        );
    }

    @Test
    void shouldMapRomanticMoodToGenres() {
        assertEquals(
                List.of(10749, 18, 35),
                moodGenreMapper.getGenreIds(Mood.ROMANTIC)
        );
    }

    @Test
    void shouldMapScaryMoodToGenres() {
        assertEquals(
                List.of(27, 53, 9648, 80),
                moodGenreMapper.getGenreIds(Mood.SCARY)
        );
    }
}