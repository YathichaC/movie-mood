package com.example.movie_mood.service.impl;

import com.example.movie_mood.domain.entity.Genre;
import com.example.movie_mood.exception.GenreNotFoundException;
import com.example.movie_mood.repository.GenreRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GenreServiceImplTest {

    private GenreRepository genreRepository;
    private GenreServiceImpl genreService;

    @BeforeEach
    void setUp() {
        genreRepository = mock(GenreRepository.class);
        genreService = new GenreServiceImpl(genreRepository);
    }

    @Test
    void createGenre_shouldSaveAndReturnGenre() {
        Genre genre = new Genre("35", "Comedy");

        when(genreRepository.save(genre)).thenReturn(genre);

        Genre result = genreService.createGenre(genre);

        assertEquals("35", result.getGenreId());
        assertEquals("Comedy", result.getGenreName());

        verify(genreRepository).save(genre);
    }

    @Test
    void getAllGenres_shouldReturnPageOfGenres() {
        Pageable pageable = PageRequest.of(0, 2);

        List<Genre> genres = List.of(
                new Genre("28", "Action"),
                new Genre("35", "Comedy")
        );

        Page<Genre> genrePage = new PageImpl<>(
                genres,
                pageable,
                genres.size()
        );

        when(genreRepository.findAll(pageable))
                .thenReturn(genrePage);

        Page<Genre> result =
                genreService.getAllGenres(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals("Action", result.getContent().get(0).getGenreName());
        assertEquals("Comedy", result.getContent().get(1).getGenreName());

        verify(genreRepository).findAll(pageable);
    }

    @Test
    void getGenreById_shouldReturnGenre_whenGenreExists() {
        Genre genre = new Genre("35", "Comedy");

        when(genreRepository.findById("35"))
                .thenReturn(Optional.of(genre));

        Genre result = genreService.getGenreById("35");

        assertEquals("35", result.getGenreId());
        assertEquals("Comedy", result.getGenreName());

        verify(genreRepository).findById("35");
    }

    @Test
    void getGenreById_shouldThrowException_whenGenreDoesNotExist() {
        when(genreRepository.findById("999"))
                .thenReturn(Optional.empty());

        GenreNotFoundException exception =
                assertThrows(
                        GenreNotFoundException.class,
                        () -> genreService.getGenreById("999")
                );

        assertEquals(
                "Genre not found with id: 999",
                exception.getMessage()
        );

        verify(genreRepository).findById("999");
    }

    @Test
    void updateGenre_shouldUpdateGenreName() {
        Genre existingGenre = new Genre("35", "Comedy");
        Genre updatedGenre = new Genre("35", "Comedy Updated");

        when(genreRepository.findById("35"))
                .thenReturn(Optional.of(existingGenre));

        when(genreRepository.save(existingGenre))
                .thenReturn(existingGenre);

        Genre result =
                genreService.updateGenre("35", updatedGenre);

        assertEquals("35", result.getGenreId());
        assertEquals("Comedy Updated", result.getGenreName());

        verify(genreRepository).findById("35");
        verify(genreRepository).save(existingGenre);
    }

    @Test
    void deleteGenre_shouldDeleteGenre_whenGenreExists() {
        Genre genre = new Genre("35", "Comedy");

        when(genreRepository.findById("35"))
                .thenReturn(Optional.of(genre));

        genreService.deleteGenre("35");

        verify(genreRepository).findById("35");
        verify(genreRepository).delete(genre);
    }
}