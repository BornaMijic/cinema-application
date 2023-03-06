package hr.tvz.mijic.cinemaapp.DTOs;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class ReservationUncompleteWithMovieEventAndCinemaHallDTO {
    private Long idReservation;
    private LocalDateTime reservationTime;
    private MovieDTO movie;
    private EventDTO event;
    private MultipleCinemaHallsDTO cinemaHall;
    private List<ReservationSeatDTO> reservationSeats;

    public ReservationUncompleteWithMovieEventAndCinemaHallDTO(Long idReservation, LocalDateTime reservationTime, MovieDTO movie, EventDTO event, MultipleCinemaHallsDTO cinemaHall, List<ReservationSeatDTO> reservationSeats) {
        this.idReservation = idReservation;
        this.reservationTime = reservationTime;
        this.movie = movie;
        this.event = event;
        this.cinemaHall = cinemaHall;
        this.reservationSeats = reservationSeats;
    }
}
