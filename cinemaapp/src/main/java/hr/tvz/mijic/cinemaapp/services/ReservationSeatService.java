package hr.tvz.mijic.cinemaapp.services;

import hr.tvz.mijic.cinemaapp.DTOs.ReservationSeatAndroidDTO;
import hr.tvz.mijic.cinemaapp.DTOs.ReservationWithSeatInfoDTO;
import hr.tvz.mijic.cinemaapp.commands.ReservationSeatCommand;

import java.util.List;

public interface ReservationSeatService {
    ReservationSeatAndroidDTO getReservationSeatByIdAndIdEvent(Long id, Long idEvent);

    List<ReservationWithSeatInfoDTO> getReservationSeatsByIdEvent(Long idEvent);

    boolean updateReservationSeat(ReservationSeatCommand reservationUpdateCommand);
}
