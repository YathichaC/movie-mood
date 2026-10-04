package com.example.movie_mood.controller.api;

import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.exception.GlobalExceptionHandler;
import com.example.movie_mood.exception.MoodNotFoundException;
import com.example.movie_mood.mapper.MoodMapper;
import com.example.movie_mood.service.MoodService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MoodControllerTest {

    @Mock
    private MoodService moodService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        MoodMapper moodMapper = new MoodMapper();
        MoodController moodController =
                new MoodController(moodService, moodMapper);

        mockMvc = MockMvcBuilders
                .standaloneSetup(moodController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAllMoodsShouldReturnAllMoods() throws Exception {
        when(moodService.getAllMoods())
                .thenReturn(List.of(
                        Mood.HAPPY,
                        Mood.SAD,
                        Mood.EXCITED,
                        Mood.RELAXED,
                        Mood.ROMANTIC,
                        Mood.SCARY
                ));

        mockMvc.perform(get("/api/moods"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$[0].name").value("HAPPY"))
                .andExpect(jsonPath("$[0].displayName").value("Happy"))
                .andExpect(jsonPath("$[5].name").value("SCARY"));
    }

    @Test
    void getMoodByNameShouldReturnMood() throws Exception {
        when(moodService.getMoodByName("happy"))
                .thenReturn(Mood.HAPPY);

        mockMvc.perform(get("/api/moods/happy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("HAPPY"))
                .andExpect(jsonPath("$.displayName").value("Happy"));
    }

    @Test
    void getMoodByNameShouldReturn404WhenMoodDoesNotExist() throws Exception {
        when(moodService.getMoodByName("ANGRY"))
                .thenThrow(new MoodNotFoundException("ANGRY"));

        mockMvc.perform(get("/api/moods/ANGRY"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Mood not found: ANGRY"))
                .andExpect(jsonPath("$.path").value("/api/moods/ANGRY"));
    }
}