package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Ahoy matey! Unit tests for the MovieService search functionality.
 * These tests ensure our treasure hunting methods work ship-shape!
 */
public class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        movieService = new MovieService();
    }

    // ===== BASIC FUNCTIONALITY TESTS =====

    @Test
    public void testGetAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        assertNotNull(movies);
        assertFalse(movies.isEmpty());
        // Should have 12 movies from movies.json
        assertEquals(12, movies.size());
    }

    @Test
    public void testGetMovieById() {
        Optional<Movie> movie = movieService.getMovieById(1L);
        assertTrue(movie.isPresent());
        assertEquals("The Prison Escape", movie.get().getMovieName());
    }

    @Test
    public void testGetMovieByIdNotFound() {
        Optional<Movie> movie = movieService.getMovieById(999L);
        assertFalse(movie.isPresent());
    }

    @Test
    public void testGetMovieByIdNull() {
        Optional<Movie> movie = movieService.getMovieById(null);
        assertFalse(movie.isPresent());
    }

    @Test
    public void testGetMovieByIdZero() {
        Optional<Movie> movie = movieService.getMovieById(0L);
        assertFalse(movie.isPresent());
    }

    // ===== SEARCH FUNCTIONALITY TESTS =====

    @Test
    public void testSearchMoviesByNameExactMatch() {
        List<Movie> results = movieService.searchMovies("The Prison Escape", null, null);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByNamePartialMatch() {
        List<Movie> results = movieService.searchMovies("Prison", null, null);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByNameCaseInsensitive() {
        List<Movie> results = movieService.searchMovies("PRISON", null, null);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByNameMultipleResults() {
        List<Movie> results = movieService.searchMovies("The", null, null);
        assertTrue(results.size() > 1);
        // Should find multiple movies with "The" in the name
        assertTrue(results.stream().allMatch(m -> m.getMovieName().toLowerCase().contains("the")));
    }

    @Test
    public void testSearchMoviesByNameNoResults() {
        List<Movie> results = movieService.searchMovies("NonExistentMovie", null, null);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesById() {
        List<Movie> results = movieService.searchMovies(null, 1L, null);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
    }

    @Test
    public void testSearchMoviesByIdNotFound() {
        List<Movie> results = movieService.searchMovies(null, 999L, null);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesByGenreExactMatch() {
        List<Movie> results = movieService.searchMovies(null, null, "Drama");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(m -> m.getGenre().toLowerCase().contains("drama")));
    }

    @Test
    public void testSearchMoviesByGenrePartialMatch() {
        List<Movie> results = movieService.searchMovies(null, null, "Crime");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(m -> m.getGenre().toLowerCase().contains("crime")));
    }

    @Test
    public void testSearchMoviesByGenreCaseInsensitive() {
        List<Movie> results = movieService.searchMovies(null, null, "DRAMA");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(m -> m.getGenre().toLowerCase().contains("drama")));
    }

    @Test
    public void testSearchMoviesByGenreNoResults() {
        List<Movie> results = movieService.searchMovies(null, null, "NonExistentGenre");
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesMultipleCriteria() {
        // Search for a specific movie using multiple criteria
        List<Movie> results = movieService.searchMovies("Family", 2L, "Crime");
        assertEquals(1, results.size());
        Movie movie = results.get(0);
        assertEquals(2L, movie.getId());
        assertTrue(movie.getMovieName().toLowerCase().contains("family"));
        assertTrue(movie.getGenre().toLowerCase().contains("crime"));
    }

    @Test
    public void testSearchMoviesMultipleCriteriaNoMatch() {
        // Search with conflicting criteria
        List<Movie> results = movieService.searchMovies("Prison", 2L, null);
        assertTrue(results.isEmpty()); // ID 2 is not "Prison" movie
    }

    @Test
    public void testSearchMoviesWithWhitespace() {
        List<Movie> results = movieService.searchMovies("  Prison  ", null, null);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesEmptyString() {
        List<Movie> results = movieService.searchMovies("", null, null);
        // Empty string should match all movies (no filter applied)
        assertEquals(12, results.size());
    }

    @Test
    public void testSearchMoviesAllNull() {
        List<Movie> results = movieService.searchMovies(null, null, null);
        // No criteria should return all movies
        assertEquals(12, results.size());
    }

    // ===== VALIDATION TESTS =====

    @Test
    public void testHasValidSearchCriteriaWithName() {
        assertTrue(movieService.hasValidSearchCriteria("Prison", null, null));
    }

    @Test
    public void testHasValidSearchCriteriaWithId() {
        assertTrue(movieService.hasValidSearchCriteria(null, 1L, null));
    }

    @Test
    public void testHasValidSearchCriteriaWithGenre() {
        assertTrue(movieService.hasValidSearchCriteria(null, null, "Drama"));
    }

    @Test
    public void testHasValidSearchCriteriaWithMultiple() {
        assertTrue(movieService.hasValidSearchCriteria("Prison", 1L, "Drama"));
    }

    @Test
    public void testHasValidSearchCriteriaAllNull() {
        assertFalse(movieService.hasValidSearchCriteria(null, null, null));
    }

    @Test
    public void testHasValidSearchCriteriaEmptyStrings() {
        assertFalse(movieService.hasValidSearchCriteria("", null, ""));
    }

    @Test
    public void testHasValidSearchCriteriaWhitespace() {
        assertFalse(movieService.hasValidSearchCriteria("   ", null, "   "));
    }

    @Test
    public void testHasValidSearchCriteriaZeroId() {
        assertFalse(movieService.hasValidSearchCriteria(null, 0L, null));
    }

    @Test
    public void testHasValidSearchCriteriaNegativeId() {
        assertFalse(movieService.hasValidSearchCriteria(null, -1L, null));
    }

    // ===== EDGE CASE TESTS =====

    @Test
    public void testSearchMoviesSpecialCharacters() {
        // Test with special characters that might be in movie names
        List<Movie> results = movieService.searchMovies(":", null, null);
        // Should find movies with colons in the name like "Space Wars: The Beginning"
        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(m -> m.getMovieName().contains(":")));
    }

    @Test
    public void testSearchMoviesGenreWithSlash() {
        // Test genre search with slash separator
        List<Movie> results = movieService.searchMovies(null, null, "Crime/Drama");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(m -> m.getGenre().contains("Crime/Drama")));
    }

    @Test
    public void testSearchMoviesPartialGenreMatch() {
        // Test that "Action" matches "Action/Crime" and "Action/Sci-Fi"
        List<Movie> results = movieService.searchMovies(null, null, "Action");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(m -> m.getGenre().toLowerCase().contains("action")));
    }

    @Test
    public void testSearchMoviesConsistentResults() {
        // Test that multiple calls return consistent results
        List<Movie> results1 = movieService.searchMovies("Drama", null, null);
        List<Movie> results2 = movieService.searchMovies("Drama", null, null);
        
        assertEquals(results1.size(), results2.size());
        for (int i = 0; i < results1.size(); i++) {
            assertEquals(results1.get(i).getId(), results2.get(i).getId());
        }
    }
}