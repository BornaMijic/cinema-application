package hr.tvz.mijic.cinemaapp.DTOs;

import lombok.Data;

@Data
public class ReservationWithSeatInfoAndFullNameDTO {
    private Long id;
    private Long userId;
    private String name;
    private String surname;
    private Long idReservationSeat;
    private Long seatId;
    private Integer seatNumber;
    private int rowNumber;

    public ReservationWithSeatInfoAndFullNameDTO(Long id, Long userId, String name, String surname, Long idReservationSeat, Long seatId, Integer seatNumber, int rowNumber) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.surname = surname;
        this.idReservationSeat = idReservationSeat;
        this.seatId = seatId;
        this.seatNumber = seatNumber;
        this.rowNumber = rowNumber;
    }
}
