package hr.tvz.mijic.cinemaapp.DTOs;

import lombok.Data;

@Data
public class ReservationWithSeatInfoDTO {
    private Long id;
    private Long idReservationSeat;
    private Long seatId;
    private Integer seatNumber;
    private int rowNumber;
    private Long userId;
    private Long eventId;

    public ReservationWithSeatInfoDTO(Long id, Long idReservationSeat, Long seatId, Integer seatNumber, int rowNumber, Long userId, Long eventId) {
        this.id = id;
        this.idReservationSeat = idReservationSeat;
        this.seatId = seatId;
        this.seatNumber = seatNumber;
        this.rowNumber = rowNumber;
        this.userId = userId;
        this.eventId = eventId;
    }


}
