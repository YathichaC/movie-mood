package com.example.movie_mood.controller.api;

import com.example.movie_mood.service.UserPreferenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserPreferenceControllerTest {

    private MockMvc mockMvc;
    private UserPreferenceService userPreferenceService;
    private UUID userId;
    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        session = new MockHttpSession();
        session.setAttribute("USER_ID", userId);

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
                .getDislikedGenreIds(userId))
                .thenReturn(List.of("28", "27"));

        mockMvc.perform(
                        get("/api/users/" + userId + "/preferences/disliked-genres")
                                .session(session)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(
                        jsonPath("$.dislikedGenreIds[0]")
                                .value("28")
                )
                .andExpect(
                        jsonPath("$.dislikedGenreIds[1]")
                                .value("27")
                );
    }

    @Test
    void updateDislikedGenresShouldReturnUpdatedPreferences()
            throws Exception {

        when(userPreferenceService
                .updateDislikedGenres(
                        userId,
                        List.of("28", "27")
                ))
                .thenReturn(List.of("28", "27"));

        mockMvc.perform(
                        put("/api/users/" + userId + "/preferences/disliked-genres")
                                .session(session)
                                .contentType("application/json")
                                .content("[\"28\",\"27\"]")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Preferences updated successfully"
                                )
                )
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(
                        jsonPath("$.dislikedGenreIds[0]")
                                .value("28")
                )
                .andExpect(
                        jsonPath("$.dislikedGenreIds[1]")
                                .value("27")
                );
    }

    @Test
    void getDislikedGenresShouldReturnBadRequestWhenUserNotFound()
            throws Exception {

        UUID randomId = UUID.randomUUID();
        MockHttpSession nonUserSession = new MockHttpSession();
        nonUserSession.setAttribute("USER_ID", randomId);

        when(userPreferenceService
                .getDislikedGenreIds(randomId))
                .thenThrow(
                        new IllegalArgumentException(
                                "User not found"
                        )
                );

        mockMvc.perform(
                        get("/api/users/" + randomId + "/preferences/disliked-genres")
                                .session(nonUserSession)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.message")
                                .value("User not found")
                );
    }
}