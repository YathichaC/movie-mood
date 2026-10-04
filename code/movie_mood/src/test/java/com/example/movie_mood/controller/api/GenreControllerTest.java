package com.example.movie_mood.controller.api;

import com.example.movie_mood.domain.entity.Genre;
import com.example.movie_mood.exception.GenreNotFoundException;
import com.example.movie_mood.exception.GlobalExceptionHandler;
import com.example.movie_mood.mapper.GenreMapper;
import com.example.movie_mood.service.GenreService;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GenreControllerTest {

    private GenreService genreService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        genreService = mock(GenreService.class);

        GenreController genreController =
                new GenreController(
                        genreService,
                        new GenreMapper()
                );

        mockMvc = MockMvcBuilders
        .standaloneSetup(genreController)
        .setCustomArgumentResolvers(
                new PageableHandlerMethodArgumentResolver()
        )
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();
    }

    @Test
    void createGenre_shouldReturn201() throws Exception {
        Genre genre = new Genre(35, "Comedy");

        when(genreService.createGenre(any(Genre.class)))
                .thenReturn(genre);

        mockMvc.perform(post("/api/v1/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "genreId": 35,
                                  "genreName": "Comedy"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.genreId").value(35))
                .andExpect(jsonPath("$.genreName").value("Comedy"));

        verify(genreService).createGenre(any(Genre.class));
    }

    @Test
    void getGenreById_shouldReturn200() throws Exception {
        Genre genre = new Genre(35, "Comedy");

        when(genreService.getGenreById(35))
                .thenReturn(genre);

        mockMvc.perform(get("/api/v1/genres/35"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.genreId").value(35))
                .andExpect(jsonPath("$.genreName").value("Comedy"));

        verify(genreService).getGenreById(35);
    }

    @Test
    void getGenreById_shouldReturn404_whenGenreDoesNotExist()
            throws Exception {

        when(genreService.getGenreById(999))
                .thenThrow(new GenreNotFoundException(999));

        mockMvc.perform(get("/api/v1/genres/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Genre not found with id: 999"));
    }

    @Test
    void getAllGenres_shouldReturnPaginatedGenres()
            throws Exception {

        Genre action = new Genre(28, "Action");
        Genre comedy = new Genre(35, "Comedy");

        PageRequest pageable = PageRequest.of(
                0,
                2,
                Sort.by("genreName").ascending()
        );

        when(genreService.getAllGenres(pageable))
                .thenReturn(
                        new PageImpl<>(
                                List.of(action, comedy),
                                pageable,
                                2
                        )
                );

        mockMvc.perform(get("/api/v1/genres")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sort", "genreName,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].genreId").value(28))
                .andExpect(jsonPath("$.content[0].genreName")
                        .value("Action"))
                .andExpect(jsonPath("$.content[1].genreId").value(35))
                .andExpect(jsonPath("$.content[1].genreName")
                        .value("Comedy"));

        verify(genreService).getAllGenres(pageable);
    }

    @Test
    void updateGenre_shouldReturn200() throws Exception {
        Genre updatedGenre =
                new Genre(35, "Updated Comedy");

        when(genreService.updateGenre(
                eq(35),
                any(Genre.class)
        )).thenReturn(updatedGenre);

        mockMvc.perform(put("/api/v1/genres/35")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "genreId": 35,
                                  "genreName": "Updated Comedy"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.genreId").value(35))
                .andExpect(jsonPath("$.genreName")
                        .value("Updated Comedy"));

        verify(genreService)
                .updateGenre(eq(35), any(Genre.class));
    }

    @Test
    void deleteGenre_shouldReturn204() throws Exception {

        doNothing()
                .when(genreService)
                .deleteGenre(35);

        mockMvc.perform(delete("/api/v1/genres/35"))
                .andExpect(status().isNoContent());

        verify(genreService).deleteGenre(35);
    }

    @Test
    void createGenre_shouldReturn400_whenGenreNameIsBlank()
            throws Exception {

        mockMvc.perform(post("/api/v1/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "genreId": 35,
                                  "genreName": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("genreName is required"));

        verifyNoInteractions(genreService);
    }
}