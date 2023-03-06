package hr.tvz.mijic.cinemaapp.services;

import hr.tvz.mijic.cinemaapp.DTOs.ReservationDTO;
import hr.tvz.mijic.cinemaapp.DTOs.ReservationUncompleteWithMovieEventAndCinemaHallDTO;
import hr.tvz.mijic.cinemaapp.DTOs.ReservationWithSeatInfoAndFullNameDTO;
import hr.tvz.mijic.cinemaapp.DTOs.ReservationWithSeatInfoDTO;
import hr.tvz.mijic.cinemaapp.commands.ReservationCommand;
import hr.tvz.mijic.cinemaapp.commands.ReservationsSaveCommand;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public interface ReservationService {
    List<ReservationWithSeatInfoDTO> getReservationsByIdEvent(Long idEvent);

    HashMap<String, Object> getReservationByIdEventAndIdUserAndUncomplete(Long idUser, Long idEvent);

    List<ReservationUncompleteWithMovieEventAndCinemaHallDTO> getAllReservationByIdUserWhichAreUncomplete(Long idUser);

    HashMap<String, Long> getCountOfUncompleteReservations(Long idUser);

    HashMap<String, Object> getReservationsByIdUser(Long idUser, int pageNumber);

    List<ReservationWithSeatInfoAndFullNameDTO> addReservation(ReservationCommand reservationCommand);

    ReservationDTO addReservations(ReservationsSaveCommand reservationCommand);

    boolean confirmReservation(Long id);

    void deleteByIdAndUserIdIfCompleteFalse(Long id, Long idUser);

    void deleteByIdAndUserId(Long id, Long idUser);

    boolean deleteReservation(Long id, Long idReservationSeat);
}
