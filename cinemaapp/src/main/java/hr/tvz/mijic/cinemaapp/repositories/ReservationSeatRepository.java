package hr.tvz.mijic.cinemaapp.repositories;

import hr.tvz.mijic.cinemaapp.entities.ReservationSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {

    @Query(value = "SELECT * FROM reservation_seats WHERE id = :id AND event_id = :idEvent", nativeQuery = true)
    ReservationSeat getReservationSeatByIdAndIdEvent(@Param("id") Long id, @Param("idEvent") Long idEvent);

    @Query(value = "SELECT * FROM reservations WHERE event_id = :idEvent", nativeQuery = true)
    List<ReservationSeat> getReservationByIdEvent(@Param("idEvent") Long idEvent);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE reservation_seats INNER JOIN reservations ON reservation_seats.reservation_id = reservations.id SET seat_id = :seatId  WHERE reservation_seats.id = :id AND reservations.complete = TRUE", nativeQuery = true)
    int updateEvent(@Param("id") Long id, @Param("seatId") Long seatId);


}
