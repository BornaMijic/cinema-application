package hr.tvz.mijic.cinemaapp.DTOs;

import lombok.Data;

import java.util.List;

@Data
public class ReservationDTO {
    private Long id;
    private Long userId;

    private List<ReservationSeatDTO> reservationSeats;

    public ReservationDTO(Long id, Long userId) {
        this.id = id;
        this.userId = userId;
    }


}
