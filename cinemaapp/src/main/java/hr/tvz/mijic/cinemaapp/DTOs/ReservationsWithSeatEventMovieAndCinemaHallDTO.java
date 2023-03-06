package hr.tvz.mijic.cinemaapp.DTOs;

import lombok.Data;

import java.util.List;

@Data
public class ReservationsWithSeatEventMovieAndCinemaHallDTO {
    private Long idReservation;
    private List<ReservationSeatDTO> reservationSeats;
    private EventDTO event;
    private MovieDTO movie;
    private MultipleCinemaHallsDTO cinemaHall;

    public ReservationsWithSeatEventMovieAndCinemaHallDTO(Long idReservation, List<ReservationSeatDTO> reservationSeats, EventDTO event, MovieDTO movie, MultipleCinemaHallsDTO cinemaHall) {
        this.idReservation = idReservation;
        this.reservationSeats = reservationSeats;
        this.event = event;
        this.movie = movie;
        this.cinemaHall = cinemaHall;
    }
}
