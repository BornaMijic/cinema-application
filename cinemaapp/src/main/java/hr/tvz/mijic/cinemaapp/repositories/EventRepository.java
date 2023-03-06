package hr.tvz.mijic.cinemaapp.repositories;

import hr.tvz.mijic.cinemaapp.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query(value = "SELECT * FROM EVENTS WHERE start_date BETWEEN DATE_ADD( UTC_TIMESTAMP() , INTERVAL -3 DAY) AND DATE_ADD( UTC_TIMESTAMP( ) , INTERVAL 14 DAY) AND movie_id = :idMovie ORDER BY start_date DESC", nativeQuery = true)
    List<Event> getActiveEventsById(@Param("idMovie") Long idMovie);

    @Query(value = "SELECT * FROM events WHERE id= :id", nativeQuery = true)
    Optional<Event> findByIdEvent(@Param("id") Long id);

    @Query(value = "SELECT * FROM events WHERE id= :id ", nativeQuery = true)
    Optional<Event> findByIdEventReservationManagment(@Param("id") Long id);

    @Query(value = "SELECT * FROM events WHERE start_date > UTC_TIMESTAMP() AND start_date < (UTC_TIMESTAMP() + INTERVAL 30 DAY) ORDER BY start_date DESC, id DESC", nativeQuery = true)
    List<Event> findEventsInNext30Days();

    @Query(value = "SELECT id, start_date, price, cinema_hall_id, movie_id FROM events WHERE movie_id = :idMovie AND start_date > UTC_TIMESTAMP() AND start_date <= (UTC_TIMESTAMP() + INTERVAL 30 DAY) ", nativeQuery = true)
    List<Event> findByIdMovie(@Param("idMovie") Long idMovie);

    @Query(value = "SELECT id, start_date, price, cinema_hall_id, movie_id FROM events WHERE movie_id = :idMovie", nativeQuery = true)
    List<Event> findByIdMovieAdmin(@Param("idMovie") Long idMovie);

    @Query(value = "SELECT id, start_date, price, cinema_hall_id, movie_id FROM events WHERE id= :id AND start_date > UTC_TIMESTAMP() AND start_date <= (UTC_TIMESTAMP() + INTERVAL 30 DAY)", nativeQuery = true)
    Event findByIdForUser(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Event SET date = :date, price = :price WHERE id = :id")
    int updateEvent(@Param("id") Long id, @Param("date") LocalDateTime date, @Param("price") Double price);

    @Override
    void deleteById(Long id);
}

