package hr.tvz.mijic.cinemaapp.repositories;

import hr.tvz.mijic.cinemaapp.entities.CinemaHall;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CinemaHallRepository extends JpaRepository<CinemaHall, Long> {

    @Query(value = "SELECT * FROM cinema_halls", nativeQuery = true)
    List<CinemaHall> findAll();

    @Query(value = "SELECT * FROM cinema_halls ORDER BY hall_name ASC", countQuery = "SELECT count(*) FROM cinema_halls", nativeQuery = true)
    Page<CinemaHall> findTenCinemaHallsPerPage(Pageable pageable);

    @Query(value = "SELECT * FROM cinema_halls WHERE LOWER(hall_name) LIKE %:searchTitle% ORDER BY hall_name ASC", countQuery = "SELECT COUNT(*) FROM cinema_halls WHERE LOWER(hall_name) LIKE %:searchTitle%", nativeQuery = true)
    Page<CinemaHall> findTenCinemaHallsPerPageWhichContainsSearchText(@Param("searchTitle") String searchTitle, Pageable pageable);

}
