package com.example.movie_mood.service.impl;

import com.example.movie_mood.domain.entity.Genre;
import com.example.movie_mood.repository.GenreRepository;
import com.example.movie_mood.service.GenreService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.movie_mood.exception.GenreNotFoundException;

@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    public GenreServiceImpl(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public Genre createGenre(Genre genre) {
        return genreRepository.save(genre);
    }

    @Override
    public Page<Genre> getAllGenres(Pageable pageable) {
        return genreRepository.findAll(pageable);
    }

    @Override
    public Genre getGenreById(String genreId) {
        return genreRepository.findById(genreId)
                .orElseThrow(() -> new GenreNotFoundException(genreId));
    }

    @Override
    public Genre updateGenre(String genreId, Genre genre) {
        Genre existingGenre = getGenreById(genreId);

        existingGenre.setGenreName(genre.getGenreName());

        return genreRepository.save(existingGenre);
    }

    @Override
    public void deleteGenre(String genreId) {
        Genre existingGenre = getGenreById(genreId);
        genreRepository.delete(existingGenre);
    }
}