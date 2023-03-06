package hr.tvz.mijic.cinemaapp.repositories;


import hr.tvz.mijic.cinemaapp.entities.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Override
    List<Reservation> findAll();

    @Query(value = "SELECT * FROM reservations WHERE event_id = :idEvent", nativeQuery = true)
    List<Reservation> getReservationByIdEvent(@Param("idEvent") Long idEvent);

    @Query(value = "SELECT reservations.* FROM reservations LEFT JOIN reservation_seats ON reservations.id = reservation_seats.reservation_id WHERE reservations.complete = FALSE AND reservations.user_id = :idUser AND  reservation_seats.event_id = :idEvent GROUP BY reservations.id", nativeQuery = true)
    Reservation getReservationByIdEventAndIdUserAndUncomplete(@Param("idUser") Long idUser, @Param("idEvent") Long idEvent);

    @Query(value = "SELECT reservations.* FROM reservations LEFT JOIN reservation_seats ON reservations.id = reservation_seats.reservation_id WHERE reservations.complete = FALSE AND reservations.user_id = :idUser GROUP BY reservations.id", nativeQuery = true)
    List<Reservation> getAllReservationByIdUserWhichAreUncomplete(@Param("idUser") Long idUser);

    @Query("SELECT COUNT(r.id) FROM Reservation r WHERE r.complete = FALSE AND r.userId = :idUser")
    Long getCountOfUncompleteReservations(@Param("idUser") Long idUser);

    @Query(value = "SELECT * FROM reservations WHERE complete = false AND (UNIX_TIMESTAMP(UTC_TIMESTAMP()) - UNIX_TIMESTAMP(reservation_time)) NOT BETWEEN -540 AND 540", nativeQuery = true)
    List<Reservation> getIncompleteReservation();

    @Query(value = "SELECT reservations.* FROM reservations LEFT JOIN events ON reservations.id = events.id  WHERE user_id = :idUser GROUP BY reservations.id ORDER BY reservations.id DESC", countQuery = "SELECT COUNT(*) FROM reservations LEFT JOIN events ON reservations.id = events.id  WHERE user_id = :idUser GROUP BY reservations.id ORDER BY reservations.id DESC", nativeQuery = true)
    Page<Reservation> getReservationByIdUser(@Param("idUser") Long idUser, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE reservations SET complete = true  WHERE id = :id", nativeQuery = true)
    int confirmReservation(@Param("id") Long id);

    @Modifying
    @Query(value = "DELETE FROM reservations WHERE id = :id AND user_id = :userId " +
            "AND complete = false", nativeQuery = true)
    void deleteByIdAndUserIdIfCompleteFalse(@Param("id") Long id, @Param("userId") Long userId);

    @Modifying
    @Query(value = "DELETE FROM reservations WHERE id = :id AND user_id = :userId AND complete = false", nativeQuery = true)
    void deleteByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Override
    void deleteById(Long id);

    @Query(value = "SELECT * FROM reservations WHERE id = :id AND COMPLETE = TRUE", nativeQuery = true)
    Reservation getReservationById(@Param("id") Long id);
}
