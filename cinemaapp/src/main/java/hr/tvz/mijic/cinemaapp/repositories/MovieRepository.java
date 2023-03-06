package hr.tvz.mijic.cinemaapp.repositories;

import hr.tvz.mijic.cinemaapp.entities.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query(value = "SELECT movies.* FROM movies INNER JOIN (SELECT * FROM events WHERE start_date BETWEEN DATE_ADD( utc_timestamp( ) , INTERVAL -3 DAY) AND DATE_ADD( utc_timestamp( ) , INTERVAL 14 DAY)) AS events ON movies.id = events.movie_id GROUP BY movies.id ORDER BY movies.id DESC", nativeQuery = true)
    List<Movie> getActiveMovies();

    @Query(value = "SELECT movies.* FROM movies " +
            "INNER JOIN (SELECT * FROM EVENTS" +
            " WHERE events.start_date > UTC_TIMESTAMP() AND " +
            "events.start_date <= (UTC_TIMESTAMP() + INTERVAL 30 DAY)) AS events " +
            "ON movies.id = events.movie_id GROUP BY movies.id " +
            "ORDER BY movies.id DESC LIMIT 3", nativeQuery = true)
    List<Movie> getLatestMovies();

    @Query(value = "SELECT movies.*, events.soon_to_start FROM movies " +
            "INNER JOIN (SELECT events.movie_id, MIN(events.start_date) soon_to_start " +
            "FROM EVENTS " +
            "WHERE events.start_date BETWEEN  UTC_TIMESTAMP() " +
            "AND (UTC_TIMESTAMP() + INTERVAL 7 DAY) " +
            "GROUP BY events.movie_id  " +
            "ORDER BY soon_to_start ASC LIMIT 3) AS EVENTS ON movies.id = events.movie_id " +
            "ORDER BY events.soon_to_start ASC", nativeQuery = true)
    List<Movie> getSoonStartingMovies();

    @Query(value = "SELECT movies.* FROM movies INNER JOIN (SELECT * FROM EVENTS WHERE events.start_date > UTC_TIMESTAMP() AND events.start_date <= (UTC_TIMESTAMP() + INTERVAL 30 DAY))  AS events ON movies.id = events.movie_id GROUP BY movies.id ORDER BY movies.id DESC", countQuery = "SELECT COUNT(*) FROM movies INNER JOIN (SELECT * FROM EVENTS WHERE events.start_date > UTC_TIMESTAMP() AND events.start_date <= (UTC_TIMESTAMP() + INTERVAL 30 DAY))  AS events ON movies.id = events.movie_id GROUP BY movies.id ORDER BY movies.id DESC", nativeQuery = true)
    Page<Movie> findFiveMoviesPerPage(Pageable pageable);

    @Query(value = "SELECT movies.* FROM movies INNER JOIN (SELECT * FROM events WHERE events.start_date > UTC_TIMESTAMP() AND events.start_date <= (UTC_TIMESTAMP() + INTERVAL 30 DAY)) AS events ON movies.id = events.movie_id WHERE LOWER(title) LIKE %:searchTitle% GROUP BY movies.id ORDER BY movies.id DESC", countQuery = "SELECT COUNT(*) FROM movies INNER JOIN (SELECT * FROM events WHERE events.start_date > UTC_TIMESTAMP() AND events.start_date <= (UTC_TIMESTAMP() + INTERVAL 30 DAY)) AS events ON movies.id = events.movie_id WHERE LOWER(title) LIKE %:searchTitle% GROUP BY movies.id ORDER BY movies.id DESC", nativeQuery = true)
    Page<Movie> findFiveMoviesPerPageWhichContainsSearchText(@Param("searchTitle") String searchTitle, Pageable pageable);


    @Query(value = "SELECT * FROM movies ORDER BY id DESC", countQuery = "SELECT count(*) FROM movies", nativeQuery = true)
    Page<Movie> findFiveMoviesPerPageAdmin(Pageable pageable);

    @Query(value = "SELECT * FROM movies WHERE LOWER(title) LIKE %:searchTitle% ORDER BY id DESC", countQuery = "SELECT COUNT(*) FROM movies WHERE LOWER(title) LIKE %:searchTitle% ORDER BY id DESC", nativeQuery = true)
    Page<Movie> findFiveMoviesPerPageWhichContainsSearchTextAdmin(@Param("searchTitle") String searchTitle, Pageable pageable);


    @Query(value = "SELECT movies.*, COUNT(*) AS number_of_events FROM movies INNER JOIN (SELECT * FROM EVENTS WHERE events.start_date > UTC_TIMESTAMP() AND events.start_date <= (UTC_TIMESTAMP() + INTERVAL 30 DAY)) AS EVENTS ON movies.id = events.movie_id WHERE movies.id = :id\n", nativeQuery = true)
    Movie findMovieByIdWhichStartsInNext30days(Long id);

    @Override
    void deleteById(Long id);
}
