package com.example.movie_mood.service.impl;

import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.domain.model.MoviePage;
import com.example.movie_mood.dto.WatchHistoryResponse;
import com.example.movie_mood.integration.tmdb.MovieProvider;
import com.example.movie_mood.mapper.MoodGenreMapper;
import com.example.movie_mood.service.RecommendationService;
import com.example.movie_mood.service.UserPreferenceService;
import com.example.movie_mood.service.WatchHistoryService;
import com.example.movie_mood.strategy.MatchScoreStrategy;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private static final int RECOMMENDATION_COUNT = 10;
    private static final int MAX_PAGES = 10;
    private static final int CANDIDATE_COUNT = 10;

    private final MovieProvider movieProvider;
    private final MoodGenreMapper moodGenreMapper;
    private final UserPreferenceService userPreferenceService;
    private final WatchHistoryService watchHistoryService;

    private final MatchScoreStrategy defaultStrategy;
    private final MatchScoreStrategy moodStrategy;
    private final MatchScoreStrategy ratingStrategy;

    public RecommendationServiceImpl(
            MovieProvider movieProvider,
            MoodGenreMapper moodGenreMapper,
            UserPreferenceService userPreferenceService,
            WatchHistoryService watchHistoryService,
            @Qualifier("defaultMatchScoreStrategy") MatchScoreStrategy defaultStrategy,
            @Qualifier("moodFocusedStrategy") MatchScoreStrategy moodStrategy,
            @Qualifier("topRatedStrategy") MatchScoreStrategy ratingStrategy) {

        this.movieProvider = movieProvider;
        this.moodGenreMapper = moodGenreMapper;
        this.userPreferenceService = userPreferenceService;
        this.watchHistoryService = watchHistoryService;

        this.defaultStrategy = defaultStrategy;
        this.moodStrategy = moodStrategy;
        this.ratingStrategy = ratingStrategy;
    }

    // รองรับ Method เดิม
    @Override
    public List<Movie> getRecommendations(Mood mood, UUID userId) {
        return getRecommendations(mood, userId, "default");
    }

    // Method ใหม่ รองรับการเลือก Strategy
    @Override
    public List<Movie> getRecommendations(
            Mood mood,
            UUID userId,
            String strategy) {

        MatchScoreStrategy selectedStrategy = switch (strategy) {
            case "mood" -> moodStrategy;
            case "rating" -> ratingStrategy;
            case "default" -> defaultStrategy;
            default -> throw new IllegalArgumentException(
                    "Unknown recommendation strategy: " + strategy);
        };

        List<Integer> moodGenreIds = moodGenreMapper.getGenreIds(mood);

        Set<Integer> dislikedGenreIds = userPreferenceService.getDislikedGenreIds(userId)
                .stream()
                .map(Integer::parseInt)
                .collect(Collectors.toSet());

        Set<String> watchedMovieIds = watchHistoryService.getUserWatchHistory(userId)
                .stream()
                .map(WatchHistoryResponse::getTmdbMovieId)
                .collect(Collectors.toSet());

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

                if (movie == null || movie.getTmdbMovieId() == null) {
                    continue;
                }

                boolean hasDislikedGenre = movie.getGenreIds() != null
                        && movie.getGenreIds().stream()
                                .anyMatch(dislikedGenreIds::contains);

                if (hasDislikedGenre
                        || watchedMovieIds.contains(movie.getTmdbMovieId())) {
                    continue;
                }

                if (seenMovieIds.add(movie.getTmdbMovieId())) {
                    recommendations.add(movie);
                }
            }

            // Stop fetching more pages when 10 candidates are available
            if (recommendations.size() >= CANDIDATE_COUNT) {
                break;
            }

            if (page >= moviePage.getTotalPages()) {
                break;
            }
        }

        // คำนวณ Match Score ตาม Strategy ที่เลือก
        for (Movie movie : recommendations) {
            double score = selectedStrategy.calculateScore(movie, mood);
            movie.setMatchScore(score);
        }

        // เรียงตาม Match Score แล้วใช้ Rating และวันที่เป็นตัวตัดสิน
        return recommendations.stream()
                .sorted(
                        Comparator.comparing(
                                Movie::getMatchScore,
                                Comparator.nullsLast(
                                        Comparator.reverseOrder()))
                                .thenComparing(
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