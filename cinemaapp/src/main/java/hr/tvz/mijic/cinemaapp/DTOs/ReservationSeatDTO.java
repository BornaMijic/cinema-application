package hr.tvz.mijic.cinemaapp.DTOs;

import lombok.Data;

@Data
public class ReservationSeatDTO {
    private Long id;
    private Long seatId;
    private int seatNumber;
    private int rowNumber;

    public ReservationSeatDTO(Long id, Long seatId, int seatNumber, int rowNumber) {
        this.id = id;
        this.seatId = seatId;
        this.seatNumber = seatNumber;
        this.rowNumber = rowNumber;
    }
}
