package hr.tvz.mijic.cinemaapp.DTOs;

import lombok.Data;

@Data
public class ReservationSeatAndroidDTO {
    private Long eventId;
    private int seatNumber;
    private int rowNumber;

    public ReservationSeatAndroidDTO(Long eventId, int seatNumber, int rowNumber) {
        this.eventId = eventId;
        this.seatNumber = seatNumber;
        this.rowNumber = rowNumber;
    }
}
