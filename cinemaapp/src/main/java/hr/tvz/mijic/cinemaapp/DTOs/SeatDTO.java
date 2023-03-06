package hr.tvz.mijic.cinemaapp.DTOs;

import lombok.Data;

@Data
public class SeatDTO {

    private Long id;
    private Integer seatNumber;
    private int rowNumber;
    private int gridPosition;

    public SeatDTO() {
    }

    public SeatDTO(Long id, Integer seatNumber, int rowNumber, int gridPosition) {
        this.id = id;
        this.seatNumber = seatNumber;
        this.rowNumber = rowNumber;
        this.gridPosition = gridPosition;
    }

}
