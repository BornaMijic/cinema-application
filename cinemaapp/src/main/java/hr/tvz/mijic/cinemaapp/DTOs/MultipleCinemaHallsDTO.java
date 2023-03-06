package hr.tvz.mijic.cinemaapp.DTOs;

import lombok.Data;

@Data
public class MultipleCinemaHallsDTO {
    private Long id;

    private String name;

    public MultipleCinemaHallsDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}
