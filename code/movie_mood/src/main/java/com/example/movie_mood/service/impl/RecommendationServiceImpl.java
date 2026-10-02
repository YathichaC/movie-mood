package com.example.movie_mood.service.impl;

import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.integration.tmdb.MovieProvider;
import com.example.movie_mood.mapper.MoodGenreMapper;
import com.example.movie_mood.service.RecommendationService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final MovieProvider movieProvider;
    private final MoodGenreMapper moodGenreMapper;

    public RecommendationServiceImpl(
            MovieProvider movieProvider,
            MoodGenreMapper moodGenreMapper) {

        this.movieProvider = movieProvider;
        this.moodGenreMapper = moodGenreMapper;
    }

    @Override
    public List<Movie> getRecommendations(
            Mood mood,
            List<Integer> dislikedGenreIds) {

        List<Integer> moodGenreIds =
                moodGenreMapper.getGenreIds(mood);

        List<Movie> movies =
                movieProvider.discoverMoviesByGenres(moodGenreIds);

        List<Integer> safeDislikedGenreIds =
                dislikedGenreIds == null
                        ? List.of()
                        : dislikedGenreIds;

        return movies.stream()

                // ตัดหนังที่มี genre ที่ user ไม่ชอบ
                .filter(movie ->
                        movie.getGenreIds() == null
                        || movie.getGenreIds()
                                .stream()
                                .noneMatch(safeDislikedGenreIds::contains)
                )

                // rating มาก -> น้อย
                // ถ้า rating เท่ากัน release date ใหม่ -> เก่า
                .sorted(
                        Comparator
                                .comparing(
                                        Movie::getRating,
                                        Comparator.nullsLast(
                                                Comparator.reverseOrder()
                                        )
                                )
                                .thenComparing(
                                        Movie::getReleaseDate,
                                        Comparator.nullsLast(
                                                Comparator.reverseOrder()
                                        )
                                )
                )

                // ไม่เกิน 10 เรื่อง
                .limit(10)
                .toList();
    }
}