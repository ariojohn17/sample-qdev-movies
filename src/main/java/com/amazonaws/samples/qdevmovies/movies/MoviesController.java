package com.amazonaws.samples.qdevmovies.movies;

import com.amazonaws.samples.qdevmovies.utils.MovieIconUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

@Controller
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movies")
    public String getMovies(org.springframework.ui.Model model) {
        logger.info("Fetching movies");
        model.addAttribute("movies", movieService.getAllMovies());
        return "movies";
    }

    @GetMapping("/movies/{id}/details")
    public String getMovieDetails(@PathVariable("id") Long movieId, org.springframework.ui.Model model) {
        logger.info("Fetching details for movie ID: {}", movieId);
        
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            logger.warn("Movie with ID {} not found", movieId);
            model.addAttribute("title", "Movie Not Found");
            model.addAttribute("message", "Movie with ID " + movieId + " was not found.");
            return "error";
        }
        
        Movie movie = movieOpt.get();
        model.addAttribute("movie", movie);
        model.addAttribute("movieIcon", MovieIconUtils.getMovieIcon(movie.getMovieName()));
        model.addAttribute("allReviews", reviewService.getReviewsForMovie(movie.getId()));
        
        return "movie-details";
    }

    /**
     * Ahoy there! Search for movie treasures using various criteria.
     * This endpoint allows ye to search by name, id, or genre - or any combination thereof!
     * 
     * @param name Movie name to search for (optional, case-insensitive partial match)
     * @param id Specific movie ID to find (optional)
     * @param genre Genre to filter by (optional, case-insensitive partial match)
     * @param model Spring model for template rendering
     * @return Template name for search results
     */
    @GetMapping("/movies/search")
    public String searchMovies(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre,
            org.springframework.ui.Model model) {
        
        logger.info("Arrr! Captain's orders to search for movies - name: '{}', id: {}, genre: '{}'", 
                   name, id, genre);
        
        // Validate that at least one search parameter is provided
        if (!movieService.hasValidSearchCriteria(name, id, genre)) {
            logger.warn("Blimey! No valid search criteria provided by the landlubber");
            model.addAttribute("movies", movieService.getAllMovies());
            model.addAttribute("searchPerformed", false);
            model.addAttribute("errorMessage", "Arrr! Ye need to provide at least one search criterion, matey!");
            return "movies";
        }
        
        // Perform the search
        List<Movie> searchResults = movieService.searchMovies(name, id, genre);
        
        // Prepare model attributes for the template
        model.addAttribute("movies", searchResults);
        model.addAttribute("searchPerformed", true);
        model.addAttribute("searchName", name);
        model.addAttribute("searchId", id);
        model.addAttribute("searchGenre", genre);
        
        if (searchResults.isEmpty()) {
            logger.info("Shiver me timbers! No treasures found matching the search criteria");
            model.addAttribute("noResults", true);
            model.addAttribute("noResultsMessage", 
                "Blimey! No movie treasures found matching yer search criteria. " +
                "Try adjusting yer search terms and set sail again!");
        } else {
            logger.info("Yo ho ho! Found {} movie treasures for the crew", searchResults.size());
            model.addAttribute("noResults", false);
            model.addAttribute("resultsMessage", 
                String.format("Ahoy! Found %d movie treasure%s matching yer search:", 
                             searchResults.size(), searchResults.size() == 1 ? "" : "s"));
        }
        
        return "movies";
    }
}