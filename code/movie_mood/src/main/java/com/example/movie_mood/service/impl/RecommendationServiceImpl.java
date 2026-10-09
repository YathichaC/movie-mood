
package com.example.movie_mood.service.impl;

import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.integration.tmdb.MovieProvider;
import com.example.movie_mood.mapper.MoodGenreMapper;
import com.example.movie_mood.service.RecommendationService;
import com.example.movie_mood.service.UserPreferenceService;
import org.springframework.stereotype.Service;
import com.example.movie_mood.domain.model.MoviePage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class RecommendationServiceImpl implements RecommendationService {

        private final MovieProvider movieProvider;
        private final MoodGenreMapper moodGenreMapper;
        private final UserPreferenceService userPreferenceService;
        private static final int RECOMMENDATION_COUNT = 10;
        private static final int MAX_PAGES = 10;

        public RecommendationServiceImpl(
                        MovieProvider movieProvider,
                        MoodGenreMapper moodGenreMapper,
                        UserPreferenceService userPreferenceService) {

                this.movieProvider = movieProvider;
                this.moodGenreMapper = moodGenreMapper;
                this.userPreferenceService = userPreferenceService;
        }

        @Override
        public List<Movie> getRecommendations(Mood mood, UUID userId) {

                List<Integer> moodGenreIds = moodGenreMapper.getGenreIds(mood);

                Set<Integer> dislikedGenreIds = userPreferenceService.getDislikedGenreIds(userId)
                                .stream()
                                .map(Integer::parseInt)
                                .collect(java.util.stream.Collectors.toSet());

                List<Movie> recommendations = new ArrayList<>();
                Set<String> seenMovieIds = new HashSet<>();

                for (int page = 1; page <= MAX_PAGES; page++) {

                        MoviePage moviePage = movieProvider.discoverMovies(
                                        moodGenreIds,
                                        null,
                                        null,
                                        null,
                                        null,
                                        page);

                        if (moviePage == null
                                        || moviePage.getMovies() == null
                                        || moviePage.getMovies().isEmpty()) {
                                break;
                        }

                        for (Movie movie : moviePage.getMovies()) {

                                if (movie == null) {
                                        continue;
                                }

                                boolean hasDislikedGenre = movie.getGenreIds() != null
                                                && movie.getGenreIds().stream()
                                                                .anyMatch(dislikedGenreIds::contains);

                                if (hasDislikedGenre) {
                                        continue;
                                }

                                if (seenMovieIds.add(movie.getTmdbMovieId())) {
                                        recommendations.add(movie);
                                }
                        }

                        if (recommendations.size() >= RECOMMENDATION_COUNT) {
                                break;
                        }

                        if (page >= moviePage.getTotalPages()) {
                                break;
                        }
                }

                return recommendations.stream()
                                .sorted(
                                                Comparator.comparing(
                                                                Movie::getRating,
                                                                Comparator.nullsLast(
                                                                                Comparator.reverseOrder()))
                                                                .thenComparing(
                                                                                Movie::getReleaseDate,
                                                                                Comparator.nullsLast(
                                                                                                Comparator.reverseOrder())))
                                .limit(RECOMMENDATION_COUNT)
                                .toList();
        }
}
