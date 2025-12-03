# Movie Service - Spring Boot Demo Application

A simple movie catalog web application built with Spring Boot, demonstrating Java application development best practices with a swashbuckling pirate theme! 🏴‍☠️

## Features

- **Movie Catalog**: Browse 12 classic movies with detailed information
- **Movie Details**: View comprehensive information including director, year, genre, duration, and description
- **🆕 Movie Search & Filtering**: Hunt for movie treasures using name, ID, or genre criteria with our pirate-themed search interface!
- **Customer Reviews**: Each movie includes authentic customer reviews with ratings and avatars
- **Responsive Design**: Mobile-first design that works on all devices
- **Modern UI**: Dark theme with gradient backgrounds and smooth animations
- **Pirate Language**: Ahoy matey! Enjoy the swashbuckling pirate terminology throughout the application

## Technology Stack

- **Java 8**
- **Spring Boot 2.0.5**
- **Maven** for dependency management
- **Log4j 2.20.0**
- **JUnit 5.8.2**
- **Thymeleaf** for templating

## Quick Start

### Prerequisites

- Java 8 or higher
- Maven 3.6+

### Run the Application

```bash
git clone https://github.com/<youruser>/sample-qdev-movies.git
cd sample-qdev-movies
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access the Application

- **Movie List**: http://localhost:8080/movies
- **Movie Details**: http://localhost:8080/movies/{id}/details (where {id} is 1-12)
- **🆕 Movie Search**: Use the search form on the movies page or directly access http://localhost:8080/movies/search with query parameters

## Building for Production

```bash
mvn clean package
java -jar target/sample-qdev-movies-0.1.0.jar
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/amazonaws/samples/qdevmovies/
│   │       ├── MoviesApplication.java    # Main Spring Boot application
│   │       ├── MoviesController.java     # REST controller for movie endpoints
│   │       ├── Movie.java                # Movie data model
│   │       ├── Review.java               # Review data model
│   │       └── utils/
│   │           ├── HTMLBuilder.java      # HTML generation utilities
│   │           └── MovieUtils.java       # Movie validation utilities
│   └── resources/
│       ├── application.yml               # Application configuration
│       ├── mock-reviews.json             # Mock review data
│       └── log4j2.xml                    # Logging configuration
└── test/                                 # Unit tests
```

## API Endpoints

### Get All Movies
```
GET /movies
```
Returns an HTML page displaying all movies with ratings and basic information, including a pirate-themed search form.

### Get Movie Details
```
GET /movies/{id}/details
```
Returns an HTML page with detailed movie information and customer reviews.

**Parameters:**
- `id` (path parameter): Movie ID (1-12)

**Example:**
```
http://localhost:8080/movies/1/details
```

### 🆕 Search Movies (Ahoy! New Feature!)
```
GET /movies/search
```
Hunt for movie treasures using various search criteria! Returns an HTML page with filtered movie results.

**Query Parameters (all optional, but at least one required):**
- `name` (string): Movie name to search for (case-insensitive partial match)
- `id` (number): Specific movie ID to find (1-12)
- `genre` (string): Genre to filter by (case-insensitive partial match)

**Examples:**
```
# Search by movie name
http://localhost:8080/movies/search?name=Prison

# Search by genre
http://localhost:8080/movies/search?genre=Drama

# Search by ID
http://localhost:8080/movies/search?id=1

# Combine multiple criteria (AND logic)
http://localhost:8080/movies/search?name=Family&genre=Crime

# Case-insensitive search
http://localhost:8080/movies/search?name=PRISON&genre=drama
```

**Response Behavior:**
- **Success**: Returns movies matching all provided criteria with pirate-themed success message
- **No Results**: Returns empty results with helpful pirate message suggesting to adjust search terms
- **Invalid Parameters**: Returns all movies with pirate error message if no valid criteria provided
- **Partial Matches**: Supports partial matching for name and genre fields

**Search Features:**
- 🔍 **Case-insensitive**: Search works regardless of letter case
- 🎯 **Partial matching**: Find movies with partial name or genre matches
- ⚓ **Multiple criteria**: Combine name, ID, and genre filters (AND logic)
- 🏴‍☠️ **Pirate messages**: Enjoy swashbuckling feedback for all search scenarios

## Troubleshooting

### Port 8080 already in use

Run on a different port:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Build failures

Clean and rebuild:
```bash
mvn clean compile
```

## Contributing

This project is designed as a demonstration application. Feel free to:
- Add more movies to the catalog
- Enhance the UI/UX with more pirate themes
- Add new features like advanced search or filtering
- Improve the responsive design
- Extend the pirate language throughout the application
- Add more search criteria (year, director, rating, etc.)

### 🏴‍☠️ Pirate Theme Guidelines
When contributing to this project, embrace the pirate spirit:
- Use pirate terminology in user-facing messages
- Add nautical emojis and symbols where appropriate
- Keep log messages and comments fun but professional
- Maintain the treasure hunting metaphor for search functionality

## License

This sample code is licensed under the MIT-0 License. See the LICENSE file.
