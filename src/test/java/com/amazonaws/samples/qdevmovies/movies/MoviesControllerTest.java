package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class MoviesControllerTest {

    private MoviesController moviesController;
    private Model model;
    private MovieService mockMovieService;
    private ReviewService mockReviewService;

    @BeforeEach
    public void setUp() {
        moviesController = new MoviesController();
        model = new ExtendedModelMap();
        
        // Create mock services
        mockMovieService = new MovieService() {
            private final List<Movie> testMovies = Arrays.asList(
                new Movie(1L, "The Prison Escape", "John Director", 1994, "Drama", "Test description", 120, 4.5),
                new Movie(2L, "The Family Boss", "Michael Filmmaker", 1972, "Crime/Drama", "Test description", 175, 5.0),
                new Movie(3L, "The Masked Hero", "Chris Moviemaker", 2008, "Action/Crime", "Test description", 152, 4.5)
            );
            
            @Override
            public List<Movie> getAllMovies() {
                return testMovies;
            }
            
            @Override
            public Optional<Movie> getMovieById(Long id) {
                return testMovies.stream().filter(m -> m.getId().equals(id)).findFirst();
            }
            
            @Override
            public List<Movie> searchMovies(String name, Long id, String genre) {
                return testMovies.stream()
                    .filter(movie -> {
                        if (id != null && !movie.getId().equals(id)) return false;
                        if (name != null && !name.trim().isEmpty() && 
                            !movie.getMovieName().toLowerCase().contains(name.toLowerCase().trim())) return false;
                        if (genre != null && !genre.trim().isEmpty() && 
                            !movie.getGenre().toLowerCase().contains(genre.toLowerCase().trim())) return false;
                        return true;
                    })
                    .collect(java.util.stream.Collectors.toList());
            }
            
            @Override
            public boolean hasValidSearchCriteria(String name, Long id, String genre) {
                boolean hasName = name != null && !name.trim().isEmpty();
                boolean hasId = id != null && id > 0;
                boolean hasGenre = genre != null && !genre.trim().isEmpty();
                return hasName || hasId || hasGenre;
            }
        };
        
        mockReviewService = new ReviewService() {
            @Override
            public List<Review> getReviewsForMovie(long movieId) {
                return new ArrayList<>();
            }
        };
        
        // Inject mocks using reflection
        try {
            java.lang.reflect.Field movieServiceField = MoviesController.class.getDeclaredField("movieService");
            movieServiceField.setAccessible(true);
            movieServiceField.set(moviesController, mockMovieService);
            
            java.lang.reflect.Field reviewServiceField = MoviesController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(moviesController, mockReviewService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock services", e);
        }
    }

    @Test
    public void testGetMovies() {
        String result = moviesController.getMovies(model);
        assertNotNull(result);
        assertEquals("movies", result);
    }

    @Test
    public void testGetMovieDetails() {
        String result = moviesController.getMovieDetails(1L, model);
        assertNotNull(result);
        assertEquals("movie-details", result);
    }

    @Test
    public void testGetMovieDetailsNotFound() {
        String result = moviesController.getMovieDetails(999L, model);
        assertNotNull(result);
        assertEquals("error", result);
    }

    @Test
    public void testMovieServiceIntegration() {
        List<Movie> movies = mockMovieService.getAllMovies();
        assertEquals(3, movies.size());
        assertEquals("The Prison Escape", movies.get(0).getMovieName());
    }

    // ===== NEW SEARCH FUNCTIONALITY TESTS =====

    @Test
    public void testSearchMoviesByName() {
        String result = moviesController.searchMovies("Prison", null, null, model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("The Prison Escape", movies.get(0).getMovieName());
        assertTrue((Boolean) model.getAttribute("searchPerformed"));
        assertFalse((Boolean) model.getAttribute("noResults"));
    }

    @Test
    public void testSearchMoviesByNameCaseInsensitive() {
        String result = moviesController.searchMovies("FAMILY", null, null, model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("The Family Boss", movies.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesById() {
        String result = moviesController.searchMovies(null, 2L, null, model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("The Family Boss", movies.get(0).getMovieName());
        assertTrue((Boolean) model.getAttribute("searchPerformed"));
        assertFalse((Boolean) model.getAttribute("noResults"));
    }

    @Test
    public void testSearchMoviesByGenre() {
        String result = moviesController.searchMovies(null, null, "Drama", model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(2, movies.size()); // "Drama" and "Crime/Drama"
        assertTrue((Boolean) model.getAttribute("searchPerformed"));
        assertFalse((Boolean) model.getAttribute("noResults"));
    }

    @Test
    public void testSearchMoviesByGenrePartialMatch() {
        String result = moviesController.searchMovies(null, null, "Crime", model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(2, movies.size()); // "Crime/Drama" and "Action/Crime"
    }

    @Test
    public void testSearchMoviesMultipleCriteria() {
        String result = moviesController.searchMovies("Family", 2L, "Crime", model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("The Family Boss", movies.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesNoResults() {
        String result = moviesController.searchMovies("NonExistentMovie", null, null, model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(0, movies.size());
        assertTrue((Boolean) model.getAttribute("searchPerformed"));
        assertTrue((Boolean) model.getAttribute("noResults"));
        assertNotNull(model.getAttribute("noResultsMessage"));
    }

    @Test
    public void testSearchMoviesInvalidCriteria() {
        String result = moviesController.searchMovies("", null, "", model);
        assertEquals("movies", result);
        
        // Should return all movies when no valid criteria provided
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(3, movies.size()); // All movies returned
        assertFalse((Boolean) model.getAttribute("searchPerformed"));
        assertNotNull(model.getAttribute("errorMessage"));
    }

    @Test
    public void testSearchMoviesNullCriteria() {
        String result = moviesController.searchMovies(null, null, null, model);
        assertEquals("movies", result);
        
        // Should return all movies when no criteria provided
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(3, movies.size());
        assertFalse((Boolean) model.getAttribute("searchPerformed"));
        assertNotNull(model.getAttribute("errorMessage"));
    }

    @Test
    public void testSearchMoviesWhitespaceCriteria() {
        String result = moviesController.searchMovies("   ", null, "   ", model);
        assertEquals("movies", result);
        
        // Should return all movies when only whitespace provided
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(3, movies.size());
        assertFalse((Boolean) model.getAttribute("searchPerformed"));
        assertNotNull(model.getAttribute("errorMessage"));
    }

    @Test
    public void testSearchMoviesInvalidId() {
        String result = moviesController.searchMovies(null, 999L, null, model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(0, movies.size());
        assertTrue((Boolean) model.getAttribute("searchPerformed"));
        assertTrue((Boolean) model.getAttribute("noResults"));
    }

    @Test
    public void testSearchMoviesModelAttributes() {
        String searchName = "Prison";
        Long searchId = 1L;
        String searchGenre = "Drama";
        
        String result = moviesController.searchMovies(searchName, searchId, searchGenre, model);
        assertEquals("movies", result);
        
        // Verify all model attributes are set correctly
        assertEquals(searchName, model.getAttribute("searchName"));
        assertEquals(searchId, model.getAttribute("searchId"));
        assertEquals(searchGenre, model.getAttribute("searchGenre"));
        assertTrue((Boolean) model.getAttribute("searchPerformed"));
        assertNotNull(model.getAttribute("resultsMessage"));
    }
}
