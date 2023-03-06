package hr.tvz.mijic.cinemaapp.DTOs;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventCinemaDTO {
    private Long id;
    private LocalDateTime date;
    private Double price;
    private Long idCinemaHall;
    private String cinemaHallName;

    public EventCinemaDTO() {
    }

    public EventCinemaDTO(Long id, LocalDateTime date,  Double price, String cinemaHallName) {
        this.id = id;
        this.date = date;
        this.price = price;
        this.cinemaHallName = cinemaHallName;
    }

    public EventCinemaDTO(Long id, LocalDateTime date, Double price, Long idCinemaHall, String cinemaHallName) {
        this.id = id;
        this.date = date;
        this.price = price;
        this.idCinemaHall = idCinemaHall;
        this.cinemaHallName = cinemaHallName;
    }
}
