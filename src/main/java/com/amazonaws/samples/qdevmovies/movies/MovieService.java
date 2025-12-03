package com.amazonaws.samples.qdevmovies.movies;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private static final Logger logger = LogManager.getLogger(MovieService.class);
    private final List<Movie> movies;
    private final Map<Long, Movie> movieMap;

    public MovieService() {
        this.movies = loadMoviesFromJson();
        this.movieMap = new HashMap<>();
        for (Movie movie : movies) {
            movieMap.put(movie.getId(), movie);
        }
    }

    private List<Movie> loadMoviesFromJson() {
        List<Movie> movieList = new ArrayList<>();
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("movies.json");
            if (inputStream != null) {
                Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
                String jsonContent = scanner.useDelimiter("\\A").next();
                scanner.close();
                
                JSONArray moviesArray = new JSONArray(jsonContent);
                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movieObj = moviesArray.getJSONObject(i);
                    movieList.add(new Movie(
                        movieObj.getLong("id"),
                        movieObj.getString("movieName"),
                        movieObj.getString("director"),
                        movieObj.getInt("year"),
                        movieObj.getString("genre"),
                        movieObj.getString("description"),
                        movieObj.getInt("duration"),
                        movieObj.getDouble("imdbRating")
                    ));
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load movies from JSON: {}", e.getMessage());
        }
        return movieList;
    }

    public List<Movie> getAllMovies() {
        return movies;
    }

    public Optional<Movie> getMovieById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(movieMap.get(id));
    }

    /**
     * Ahoy matey! Search for treasure (movies) in our vast collection using various criteria.
     * This method filters movies based on name, id, and genre parameters.
     * 
     * @param name Movie name to search for (case-insensitive partial match)
     * @param id Specific movie ID to find
     * @param genre Genre to filter by (case-insensitive partial match)
     * @return List of movies matching the search criteria
     */
    public List<Movie> searchMovies(String name, Long id, String genre) {
        logger.info("Arrr! Starting treasure hunt for movies with criteria - name: '{}', id: {}, genre: '{}'", 
                   name, id, genre);
        
        List<Movie> results = movies.stream()
            .filter(movie -> matchesSearchCriteria(movie, name, id, genre))
            .collect(Collectors.toList());
        
        logger.info("Shiver me timbers! Found {} treasures matching yer search criteria", results.size());
        return results;
    }

    /**
     * Determines if a movie matches the search criteria like a skilled pirate navigator
     * checking if treasure matches the map description.
     * 
     * @param movie The movie to check
     * @param name Name criteria (null or empty means no name filter)
     * @param id ID criteria (null means no ID filter)
     * @param genre Genre criteria (null or empty means no genre filter)
     * @return true if movie matches all provided criteria
     */
    private boolean matchesSearchCriteria(Movie movie, String name, Long id, String genre) {
        // Check ID match first - most specific criteria
        if (id != null && !movie.getId().equals(id)) {
            return false;
        }
        
        // Check name match - case insensitive partial match
        if (name != null && !name.trim().isEmpty()) {
            String movieName = movie.getMovieName().toLowerCase();
            String searchName = name.toLowerCase().trim();
            if (!movieName.contains(searchName)) {
                return false;
            }
        }
        
        // Check genre match - case insensitive partial match
        if (genre != null && !genre.trim().isEmpty()) {
            String movieGenre = movie.getGenre().toLowerCase();
            String searchGenre = genre.toLowerCase().trim();
            if (!movieGenre.contains(searchGenre)) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Batten down the hatches! Validate search parameters to prevent scurvy bugs.
     * 
     * @param name Movie name parameter
     * @param id Movie ID parameter  
     * @param genre Genre parameter
     * @return true if at least one valid search parameter is provided
     */
    public boolean hasValidSearchCriteria(String name, Long id, String genre) {
        boolean hasName = name != null && !name.trim().isEmpty();
        boolean hasId = id != null && id > 0;
        boolean hasGenre = genre != null && !genre.trim().isEmpty();
        
        boolean isValid = hasName || hasId || hasGenre;
        
        if (!isValid) {
            logger.warn("Arrr! No valid search criteria provided - all parameters be empty or null!");
        }
        
        return isValid;
    }
}
