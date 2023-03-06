package hr.tvz.mijic.cinemaapp.DTOs;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventDTO {
    private Long id;
    private LocalDateTime date;
    private Double price;
    private Long idMovie;
    private Long idCinemaHall;

    public EventDTO() {
    }


    public EventDTO(Long id, LocalDateTime date, Double price, Long idMovie, Long idCinemaHall) {
        this.id = id;
        this.date = date;
        this.price = price;
        this.idMovie = idMovie;
        this.idCinemaHall = idCinemaHall;
    }
}
