package com.example.movie_mood.service;

import com.example.movie_mood.domain.entity.Genre;
import com.example.movie_mood.domain.entity.User;
import com.example.movie_mood.domain.entity.UserDislikedGenre;
import com.example.movie_mood.repository.GenreRepository;
import com.example.movie_mood.repository.UserDislikedGenreRepository;
import com.example.movie_mood.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserPreferenceService {

        private final UserRepository userRepository;
        private final GenreRepository genreRepository;
        private final UserDislikedGenreRepository userDislikedGenreRepository;

        public UserPreferenceService(
                        UserRepository userRepository,
                        GenreRepository genreRepository,
                        UserDislikedGenreRepository userDislikedGenreRepository) {

                this.userRepository = userRepository;
                this.genreRepository = genreRepository;
                this.userDislikedGenreRepository = userDislikedGenreRepository;
        }

        public List<String> getDislikedGenreIds(UUID userId) {

                if (!userRepository.existsById(userId)) {
                        throw new IllegalArgumentException("User not found");
                }

                return userDislikedGenreRepository
                                .findByUserUserId(userId)
                                .stream()
                                .map(userDislikedGenre -> userDislikedGenre.getGenre().getGenreId())
                                .toList();
        }

        @Transactional
        public List<String> updateDislikedGenres(
                        UUID userId,
                        List<String> genreIds) {

                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new IllegalArgumentException("User not found"));

                userDislikedGenreRepository.deleteByUserUserId(userId);

                userDislikedGenreRepository.flush();

                for (String genreId : genreIds) {

                        Genre genre = genreRepository.findById(genreId)
                                        .orElseThrow(() -> new IllegalArgumentException(
                                                        "Genre not found: " + genreId));

                        UserDislikedGenre preference = new UserDislikedGenre(user, genre);

                        userDislikedGenreRepository.save(preference);
                }

                return genreIds;
        }
}