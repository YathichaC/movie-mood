package com.example.movie_mood.controller.api;

import com.example.movie_mood.service.UserPreferenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserPreferenceControllerTest {

    private MockMvc mockMvc;
    private UserPreferenceService userPreferenceService;

    @BeforeEach
    void setUp() {

        userPreferenceService =
                Mockito.mock(UserPreferenceService.class);

        UserPreferenceController controller =
                new UserPreferenceController(
                        userPreferenceService
                );

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void getDislikedGenresShouldReturnPreferences()
            throws Exception {

        when(userPreferenceService
                .getDislikedGenreIds(1))
                .thenReturn(List.of(28, 27));

        mockMvc.perform(
                        get("/api/users/1/preferences/disliked-genres")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(
                        jsonPath("$.dislikedGenreIds[0]")
                                .value(28)
                )
                .andExpect(
                        jsonPath("$.dislikedGenreIds[1]")
                                .value(27)
                );
    }

    @Test
    void updateDislikedGenresShouldReturnUpdatedPreferences()
            throws Exception {

        when(userPreferenceService
                .updateDislikedGenres(
                        1,
                        List.of(28, 27)
                ))
                .thenReturn(List.of(28, 27));

        mockMvc.perform(
                        put("/api/users/1/preferences/disliked-genres")
                                .contentType("application/json")
                                .content("[28,27]")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Preferences updated successfully"
                                )
                )
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(
                        jsonPath("$.dislikedGenreIds[0]")
                                .value(28)
                )
                .andExpect(
                        jsonPath("$.dislikedGenreIds[1]")
                                .value(27)
                );
    }

    @Test
    void getDislikedGenresShouldReturnBadRequestWhenUserNotFound()
            throws Exception {

        when(userPreferenceService
                .getDislikedGenreIds(999))
                .thenThrow(
                        new IllegalArgumentException(
                                "User not found"
                        )
                );

        mockMvc.perform(
                        get("/api/users/999/preferences/disliked-genres")
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.message")
                                .value("User not found")
                );
    }
}