package com.example.movie_mood.strategy;

import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.service.MovieService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Component("moodStrategy")
public class MoodStrategy implements Strategy {

    private final MovieService movieService;

    public MoodStrategy(MovieService movieService) {
        this.movieService = movieService;
    }

    @Override
    public List<Movie> recommend(Mood mood, List<Integer> dislikedGenreIds) {
        List<Movie> allMovies = movieService.browseMovies();
        if (allMovies == null || allMovies.isEmpty()) {
            allMovies = getFallbackMockMovies();
        }

        List<Movie> recommended = new ArrayList<>();

        for (Movie movie : allMovies) {
            // 1. ข้ามแนวหนังที่ไม่ชอบ
            if (dislikedGenreIds != null && isDisliked(movie, dislikedGenreIds)) {
                continue;
            }

            // 2. คำนวณ Match Score ตาม Mood และ Rating
            double score = calculateMatchScore(movie, mood);
            movie.setMatchScore(score);

            recommended.add(movie);
        }

        // 3. จัดเรียง: MatchScore มาก -> Rating สูง -> หนังใหม่กว่า -> ชื่อเรื่อง A-Z
        recommended.sort(
            Comparator.comparing(Movie::getMatchScore, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(Movie::getRating, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(Movie::getReleaseDate, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(Movie::getTitle, Comparator.nullsLast(Comparator.naturalOrder()))
        );

        return recommended;
    }

    private double calculateMatchScore(Movie movie, Mood mood) {
        double score = 40.0; // คะแนนฐาน

        // คะแนนจากเรตติ้ง (สูงสุดประมาณ +30 ถึง +40 คะแนน)
        if (movie.getRating() != null) {
            score += Math.min(movie.getRating() * 4.0, 40.0);
        }

        // โบนัสพิเศษเมื่อตรงกับ Mood (+25 คะแนน)
        List<Integer> targetGenres = getGenreIdsForMood(mood);
        if (movie.getGenreIds() != null && !Collections.disjoint(movie.getGenreIds(), targetGenres)) {
            score += 25.0;
        }

        return Math.min(score, 100.0);
    }

    private List<Integer> getGenreIdsForMood(Mood mood) {
        if (mood == null) return List.of();
        return switch (mood) {
            case ROMANTIC -> List.of(10749, 18);          // Romance, Drama
            case HAPPY -> List.of(35, 16, 10751);         // Comedy, Animation, Family
            case SAD -> List.of(18);                      // Drama
            case EXCITED -> List.of(28, 12, 878, 53);     // Action, Adventure, Sci-Fi, Thriller
            case SCARY -> List.of(27, 9648, 53);          // Horror, Mystery, Thriller
            case RELAXED -> List.of(16, 99, 10402);       // Animation, Documentary, Music
        };
    }

    private boolean isDisliked(Movie movie, List<Integer> dislikedGenreIds) {
        if (movie.getGenreIds() == null) return false;
        return movie.getGenreIds().stream().anyMatch(dislikedGenreIds::contains);
    }

    private List<Movie> getFallbackMockMovies() {
        List<Movie> list = new ArrayList<>();

        Movie m1 = new Movie();
        m1.setTitle("Inside Out 2");
        m1.setRating(7.8);
        m1.setGenreIds(List.of(16, 35));
        list.add(m1);

        Movie m2 = new Movie();
        m2.setTitle("About Time");
        m2.setRating(8.2);
        m2.setGenreIds(List.of(10749, 18));
        list.add(m2);

        Movie m3 = new Movie();
        m3.setTitle("A Quiet Place: Day One");
        m3.setRating(6.5);
        m3.setGenreIds(List.of(27, 53));
        list.add(m3);

        return list;
    }
}