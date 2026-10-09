package com.example.movie_mood.service.impl;

import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.exception.MoodNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MoodServiceImplTest {

    private MoodServiceImpl moodService;

    @BeforeEach
    void setUp() {
        moodService = new MoodServiceImpl();
    }

    @Test
    void getAllMoodsShouldReturnAllSixMoods() {
        List<Mood> moods = moodService.getAllMoods();

        assertEquals(6, moods.size());
        assertTrue(moods.contains(Mood.HAPPY));
        assertTrue(moods.contains(Mood.SAD));
        assertTrue(moods.contains(Mood.EXCITED));
        assertTrue(moods.contains(Mood.RELAXED));
        assertTrue(moods.contains(Mood.ROMANTIC));
        assertTrue(moods.contains(Mood.SCARY));
    }

    @Test
    void getMoodByNameShouldReturnMoodForUppercaseName() {
        Mood mood = moodService.getMoodByName("HAPPY");

        assertEquals(Mood.HAPPY, mood);
    }

    @Test
    void getMoodByNameShouldBeCaseInsensitive() {
        Mood mood = moodService.getMoodByName("happy");

        assertEquals(Mood.HAPPY, mood);
    }

    @Test
    void getMoodByNameShouldIgnoreSurroundingWhitespace() {
        Mood mood = moodService.getMoodByName("  relaxed  ");

        assertEquals(Mood.RELAXED, mood);
    }

    @Test
    void getMoodByNameShouldThrowWhenMoodDoesNotExist() {
        MoodNotFoundException exception = assertThrows(
                MoodNotFoundException.class,
                () -> moodService.getMoodByName("ANGRY")
        );

        assertEquals("Mood not found: ANGRY", exception.getMessage());
    }

    @Test
    void getMoodByNameShouldThrowWhenNameIsBlank() {
        assertThrows(
                MoodNotFoundException.class,
                () -> moodService.getMoodByName(" ")
        );
    }
}