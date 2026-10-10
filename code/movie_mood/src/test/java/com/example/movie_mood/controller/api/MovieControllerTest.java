package com.example.movie_mood.controller.api;

import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.exception.GlobalExceptionHandler;
import com.example.movie_mood.facade.MovieDetailFacade;
import com.example.movie_mood.mapper.MovieMapper;
import com.example.movie_mood.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.example.movie_mood.domain.model.MoviePage;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MovieControllerTest {

        private MovieService movieService;
        private MovieDetailFacade movieDetailFacade;
        private MockMvc mockMvc;

        @BeforeEach
        void setUp() {
                movieService = mock(MovieService.class);
                movieDetailFacade = mock(MovieDetailFacade.class);
                MovieMapper movieMapper = new MovieMapper();

                MovieController controller = new MovieController(
                                movieService,
                                movieDetailFacade,
                                movieMapper);

                mockMvc = MockMvcBuilders
                                .standaloneSetup(controller)
                                .setControllerAdvice(new GlobalExceptionHandler())
                                .build();
        }

        @Test
        void filterMoviesByGenre_shouldReturnMovies() throws Exception {
                Movie movie = new Movie();
                movie.setTmdbMovieId("1");
                movie.setTitle("Comedy Movie");
                movie.setRating(8.0);
                movie.setGenreIds(List.of(35));

                when(movieService.filterMoviesByGenre(35))
                                .thenReturn(List.of(movie));

                mockMvc.perform(
                                get("/api/v1/movies/filter")
                                                .param("genreId", "35"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].tmdbMovieId").value("1"))
                                .andExpect(jsonPath("$[0].title").value("Comedy Movie"))
                                .andExpect(jsonPath("$[0].rating").value(8.0))
                                .andExpect(jsonPath("$[0].genreIds[0]").value(35));

                verify(movieService).filterMoviesByGenre(35);
        }

        @Test
        void filterMoviesByMood_shouldReturnMovies() throws Exception {
                Movie movie = new Movie();
                movie.setTmdbMovieId("2");
                movie.setTitle("Happy Movie");
                movie.setRating(7.5);
                movie.setGenreIds(List.of(35, 16));

                when(movieService.filterMoviesByMood(Mood.HAPPY))
                                .thenReturn(List.of(movie));

                mockMvc.perform(
                                get("/api/v1/movies/filter/mood")
                                                .param("mood", "HAPPY"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].tmdbMovieId").value("2"))
                                .andExpect(jsonPath("$[0].title").value("Happy Movie"))
                                .andExpect(jsonPath("$[0].rating").value(7.5))
                                .andExpect(jsonPath("$[0].genreIds[0]").value(35))
                                .andExpect(jsonPath("$[0].genreIds[1]").value(16));

                verify(movieService)
                                .filterMoviesByMood(Mood.HAPPY);
        }

        @Test
        void filterMoviesByMood_withInvalidMood_shouldReturnBadRequest()
                        throws Exception {

                mockMvc.perform(
                                get("/api/v1/movies/filter/mood")
                                                .param("mood", "ANGRY"))
                                .andExpect(status().isBadRequest());

                verifyNoInteractions(movieService);
        }

        @Test
        void browseMovies_shouldReturnMovies() throws Exception {
                Movie movie = new Movie();
                movie.setTmdbMovieId("10");
                movie.setTitle("Popular Movie");
                movie.setRating(8.2);
                movie.setGenreIds(List.of(28, 12));

                MoviePage moviePage = new MoviePage(
                                List.of(movie),
                                1,
                                10,
                                200);

                when(movieService.browseMovies(1))
                                .thenReturn(moviePage);

                mockMvc.perform(
                                get("/api/v1/movies")
                                                .param("page", "1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content[0].tmdbMovieId").value("10"))
                                .andExpect(jsonPath("$.content[0].title").value("Popular Movie"))
                                .andExpect(jsonPath("$.content[0].rating").value(8.2))
                                .andExpect(jsonPath("$.content[0].genreIds[0]").value(28))
                                .andExpect(jsonPath("$.content[0].genreIds[1]").value(12))
                                .andExpect(jsonPath("$.page").value(1))
                                .andExpect(jsonPath("$.totalPages").value(10))
                                .andExpect(jsonPath("$.totalElements").value(200));

                verify(movieService).browseMovies(1);
        }

        @Test
        void searchMovies_shouldReturnMatchingMovies() throws Exception {

                // 1. สร้างหนังจำลอง
                Movie movie = new Movie();
                movie.setTmdbMovieId("11");
                movie.setTitle("Batman");
                movie.setRating(8.0);
                movie.setGenreIds(List.of(28));

                // 2. สร้างผลลัพธ์จำลอง
                MoviePage moviePage = new MoviePage(
                                List.of(movie),
                                1,
                                1,
                                1);

                // 3. Mock ให้ตรงกับค่าที่ Controller ส่งจริง
                when(movieService.searchMoviesWithFilters(
                                eq("Batman"),
                                isNull(),
                                isNull(),
                                isNull(),
                                isNull(),
                                isNull(),
                                eq(1))).thenReturn(moviePage);

                // 4. ทดสอบ API
                mockMvc.perform(
                                get("/api/v1/movies/search")
                                                .param("keyword", "Batman")
                                                .param("page", "1"))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content[0].tmdbMovieId").value("11"))
                                .andExpect(jsonPath("$.content[0].title").value("Batman"))
                                .andExpect(jsonPath("$.content[0].rating").value(8.0))
                                .andExpect(jsonPath("$.page").value(1))
                                .andExpect(jsonPath("$.totalPages").value(1))
                                .andExpect(jsonPath("$.totalElements").value(1));

                // 5. ตรวจสอบว่า Controller เรียก Service ถูกต้อง
                verify(movieService).searchMoviesWithFilters(
                                eq("Batman"),
                                isNull(),
                                isNull(),
                                isNull(),
                                isNull(),
                                isNull(),
                                eq(1));
        }

        @Test
        void discoverMovies_shouldReturnMovies() throws Exception {
                Movie movie = new Movie();
                movie.setTmdbMovieId("100");
                movie.setTitle("Action Movie");
                movie.setRating(8.5);
                movie.setGenreIds(List.of(28));

                MoviePage moviePage = new MoviePage(
                                List.of(movie),
                                1,
                                5,
                                100);

                when(movieService.discoverMovies(
                                List.of(28, 35),
                                2020,
                                2026,
                                7.0,
                                "rating_desc",
                                1))
                                .thenReturn(moviePage);

                mockMvc.perform(
                                get("/api/v1/movies/discover")
                                                .param("genreIds", "28,35")
                                                .param("startYear", "2020")
                                                .param("endYear", "2026")
                                                .param("minRating", "7.0")
                                                .param("sortBy", "rating_desc")
                                                .param("page", "1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content[0].tmdbMovieId")
                                                .value("100"))
                                .andExpect(jsonPath("$.content[0].title")
                                                .value("Action Movie"))
                                .andExpect(jsonPath("$.content[0].rating")
                                                .value(8.5))
                                .andExpect(jsonPath("$.content[0].genreIds[0]")
                                                .value(28))
                                .andExpect(jsonPath("$.page").value(1))
                                .andExpect(jsonPath("$.totalPages").value(5))
                                .andExpect(jsonPath("$.totalElements").value(100));

                verify(movieService).discoverMovies(
                                List.of(28, 35),
                                2020,
                                2026,
                                7.0,
                                "rating_desc",
                                1);
        }

        @Test
        void getMovieDetails_shouldReturnMovie() throws Exception {
                Movie movie = new Movie();
                movie.setTmdbMovieId("550");
                movie.setTitle("Fight Club");
                movie.setRating(8.4);
                movie.setGenreIds(List.of(18, 53));
                movie.setPosterPath("/fight-club-poster.jpg");
                movie.setBackdropPath("/fight-club-backdrop.jpg");

                when(movieDetailFacade.getMovieDetails("550"))
                                .thenReturn(movie);

                mockMvc.perform(get("/api/v1/movies/550"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.tmdbMovieId").value("550"))
                                .andExpect(jsonPath("$.title").value("Fight Club"))
                                .andExpect(jsonPath("$.rating").value(8.4))
                                .andExpect(jsonPath("$.genreIds[0]").value(18))
                                .andExpect(jsonPath("$.genreIds[1]").value(53))
                                .andExpect(jsonPath("$.posterPath")
                                                .value("/fight-club-poster.jpg"))
                                .andExpect(jsonPath("$.backdropPath")
                                                .value("/fight-club-backdrop.jpg"));

                verify(movieDetailFacade).getMovieDetails("550");
                verifyNoInteractions(movieService);
        }

        @Test
        void getMovieBatch_shouldReturnMovieDetailsForSelectedIds() throws Exception {
                Movie firstMovie = new Movie();
                firstMovie.setTmdbMovieId("1");
                firstMovie.setTitle("First Movie");
                firstMovie.setPosterPath("/first.jpg");

                Movie secondMovie = new Movie();
                secondMovie.setTmdbMovieId("2");
                secondMovie.setTitle("Second Movie");
                secondMovie.setPosterPath("/second.jpg");

                when(movieService.getMovieBatch(List.of("1", "2")))
                                .thenReturn(List.of(firstMovie, secondMovie));

                mockMvc.perform(post("/api/v1/movies/batch")
                                .contentType("application/json")
                                .content("[\"1\",\"2\"]"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value("1"))
                                .andExpect(jsonPath("$[0].name").value("First Movie"))
                                .andExpect(jsonPath("$[0].poster_path").value("/first.jpg"))
                                .andExpect(jsonPath("$[1].id").value("2"))
                                .andExpect(jsonPath("$[1].name").value("Second Movie"));

                verify(movieService).getMovieBatch(List.of("1", "2"));
        }
}
