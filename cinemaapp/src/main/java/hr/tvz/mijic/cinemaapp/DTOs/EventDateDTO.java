package hr.tvz.mijic.cinemaapp.DTOs;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventDateDTO {

    private Long id;

    private LocalDateTime date;


    public EventDateDTO(Long id, LocalDateTime date) {
        this.id = id;
        this.date = date;
    }
}
