package com.example.movie_mood.integration.tmdb.proxy;

import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.domain.model.Video;
import com.example.movie_mood.integration.tmdb.MovieProvider;
import com.example.movie_mood.integration.tmdb.TmdbMovieAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import com.example.movie_mood.domain.model.MoviePage;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Primary
public class CachingMovieServiceProxy implements MovieProvider {

    private static final Logger log = LoggerFactory.getLogger(CachingMovieServiceProxy.class);
    private static final long POPULAR_MOVIES_CACHE_TTL_MS = Duration.ofMinutes(5).toMillis();
    private static final long DISCOVER_MOVIES_CACHE_TTL_MS = Duration.ofMinutes(5).toMillis();
    private static final int MAX_DISCOVER_CACHE_ENTRIES = 256;
    private static final long MOVIE_CACHE_TTL_MS = Duration.ofMinutes(30).toMillis();
    private static final int MAX_MOVIE_CACHE_ENTRIES = 512;
    private final AtomicLong discoverMoviesCacheHits = new AtomicLong();
    private final AtomicLong discoverMoviesCacheMisses = new AtomicLong();

    private final TmdbMovieAdapter movieAdapter;

    private final Map<String, CacheEntry> movieCache = Collections.synchronizedMap(
            new LinkedHashMap<String, CacheEntry>(128, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, CacheEntry> eldest) {
                    return size() > MAX_MOVIE_CACHE_ENTRIES;
                }
            });
    private final Map<String, CachedMoviePage> popularMoviesCache = Collections.synchronizedMap(new LinkedHashMap<>());
    private final Map<String, CachedMoviePage> discoverMoviesCache = Collections.synchronizedMap(
            new LinkedHashMap<String, CachedMoviePage>(
                    128, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(
                        Map.Entry<String, CachedMoviePage> eldest) {
                    return size() > MAX_DISCOVER_CACHE_ENTRIES;
                }
            });
    private final AtomicLong movieCacheHits = new AtomicLong();
    private final AtomicLong movieCacheMisses = new AtomicLong();
    private final AtomicLong popularMoviesCacheHits = new AtomicLong();
    private final AtomicLong popularMoviesCacheMisses = new AtomicLong();

    public CachingMovieServiceProxy(
            TmdbMovieAdapter movieAdapter) {
        this.movieAdapter = movieAdapter;
    }

    @Override
    public MoviePage getPopularMovies(int page) {
        String cacheKey = "popular:" + page;
        long now = System.currentTimeMillis();

        CachedMoviePage cachedResponse = popularMoviesCache.get(cacheKey);

        if (cachedResponse != null && cachedResponse.expiresAt() > now) {
            popularMoviesCacheHits.incrementAndGet();
            return cachedResponse.moviePage();
        }

        if (cachedResponse != null) {
            popularMoviesCache.remove(cacheKey, cachedResponse);
        }

        popularMoviesCacheMisses.incrementAndGet();

        MoviePage moviePage = movieAdapter.getPopularMovies(page);

        if (moviePage != null) {
            popularMoviesCache.put(
                    cacheKey,
                    new CachedMoviePage(
                            moviePage,
                            System.currentTimeMillis() + POPULAR_MOVIES_CACHE_TTL_MS));
        }

        return moviePage;
    }

    @Override
    public MoviePage searchMovies(String keyword, int page) {
        return movieAdapter.searchMovies(keyword, page);
    }

    @Override
    public Movie getMovie(String tmdbMovieId) {
        long now = System.currentTimeMillis();
        CacheEntry cachedEntry = movieCache.get(tmdbMovieId);
        if (cachedEntry != null) {
            if (cachedEntry.expiresAt() <= now) {
                movieCache.remove(tmdbMovieId);
            } else {
                movieCacheHits.incrementAndGet();
                return cachedEntry.movie();
            }
        }

        movieCacheMisses.incrementAndGet();
        Movie loadedMovie = movieAdapter.getMovie(tmdbMovieId);
        if (loadedMovie != null) {
            movieCache.put(tmdbMovieId, new CacheEntry(loadedMovie, now + MOVIE_CACHE_TTL_MS));
        }
        return loadedMovie;
    }

    public CacheMetrics getCacheMetrics() {
        return new CacheMetrics(
                movieCacheHits.get(),
                movieCacheMisses.get(),
                popularMoviesCacheHits.get(),
                popularMoviesCacheMisses.get(),
                discoverMoviesCacheHits.get(),
                discoverMoviesCacheMisses.get());
    }

    public CacheMetrics snapshotAndResetCacheMetrics() {
        CacheMetrics metrics = getCacheMetrics();

        movieCacheHits.set(0);
        movieCacheMisses.set(0);
        popularMoviesCacheHits.set(0);
        popularMoviesCacheMisses.set(0);
        discoverMoviesCacheHits.set(0);
        discoverMoviesCacheMisses.set(0);

        if (metrics.movieHits() > 0
                || metrics.movieMisses() > 0
                || metrics.popularHits() > 0
                || metrics.popularMisses() > 0
                || metrics.discoverHits() > 0
                || metrics.discoverMisses() > 0) {

            log.info(
                    "TMDB cache summary movieHits={} movieMisses={} "
                            + "popularHits={} popularMisses={} "
                            + "discoverHits={} discoverMisses={}",
                    metrics.movieHits(),
                    metrics.movieMisses(),
                    metrics.popularHits(),
                    metrics.popularMisses(),
                    metrics.discoverHits(),
                    metrics.discoverMisses());
        }

        return metrics;
    }

    private record CachedMoviePage(MoviePage moviePage, long expiresAt) {
    }

    private record CacheEntry(Movie movie, long expiresAt) {
    }

    public record CacheMetrics(
            long movieHits,
            long movieMisses,
            long popularHits,
            long popularMisses,
            long discoverHits,
            long discoverMisses) {
    }

    @Override
    public List<Video> getMovieVideos(String tmdbMovieId) {
        return movieAdapter.getMovieVideos(tmdbMovieId);
    }

    @Override
    public List<Movie> discoverMoviesByGenres(
            List<Integer> genreIds) {

        return movieAdapter.discoverMoviesByGenres(genreIds);
    }

    @Override
    public MoviePage discoverMovies(
            List<Integer> genreIds,
            Integer startYear,
            Integer endYear,
            Double minRating,
            String sortBy,
            int page) {

        // Normalize genre IDs so equivalent queries share the same cache key
        List<Integer> normalizedGenreIds = genreIds == null
                ? Collections.emptyList()
                : genreIds.stream()
                        .sorted()
                        .toList();

        String cacheKey = "discover:"
                + normalizedGenreIds
                + ":" + startYear
                + ":" + endYear
                + ":" + minRating
                + ":" + sortBy
                + ":" + page;

        long now = System.currentTimeMillis();

        CachedMoviePage cachedResponse = discoverMoviesCache.get(cacheKey);

        if (cachedResponse != null
                && cachedResponse.expiresAt() > now) {
            discoverMoviesCacheHits.incrementAndGet();
            return cachedResponse.moviePage();
        }

        if (cachedResponse != null) {
            discoverMoviesCache.remove(cacheKey, cachedResponse);
        }

        discoverMoviesCacheMisses.incrementAndGet();

        MoviePage moviePage = movieAdapter.discoverMovies(
                genreIds,
                startYear,
                endYear,
                minRating,
                sortBy,
                page);

        if (moviePage != null) {
            discoverMoviesCache.put(
                    cacheKey,
                    new CachedMoviePage(
                            moviePage,
                            System.currentTimeMillis()
                                    + DISCOVER_MOVIES_CACHE_TTL_MS));
        }

        return moviePage;
    }
}