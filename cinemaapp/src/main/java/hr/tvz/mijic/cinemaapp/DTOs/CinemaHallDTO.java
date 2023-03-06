package hr.tvz.mijic.cinemaapp.DTOs;

import lombok.Data;

import java.util.List;

@Data
public class CinemaHallDTO {
    private String name;

    private int gridRowsNumber;

    private int gridColumnsNumber;

    private List<SeatDTO> seats;

    public CinemaHallDTO() {
    }

    public CinemaHallDTO(String name, int gridRowsNumber, int gridColumnsNumber, List<SeatDTO> seats) {
        this.name = name;
        this.gridRowsNumber= gridRowsNumber;
        this.gridColumnsNumber = gridColumnsNumber;
        this.seats = seats;
    }
}
