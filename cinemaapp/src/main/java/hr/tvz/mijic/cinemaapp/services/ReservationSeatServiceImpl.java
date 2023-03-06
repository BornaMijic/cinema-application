package hr.tvz.mijic.cinemaapp.services;

import hr.tvz.mijic.cinemaapp.DTOs.ReservationSeatAndroidDTO;
import hr.tvz.mijic.cinemaapp.DTOs.ReservationWithSeatInfoDTO;
import hr.tvz.mijic.cinemaapp.commands.ReservationSeatCommand;
import hr.tvz.mijic.cinemaapp.entities.Event;
import hr.tvz.mijic.cinemaapp.entities.Reservation;
import hr.tvz.mijic.cinemaapp.entities.ReservationSeat;
import hr.tvz.mijic.cinemaapp.entities.Seat;
import hr.tvz.mijic.cinemaapp.repositories.ReservationSeatRepository;
import hr.tvz.mijic.cinemaapp.repositories.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservationSeatServiceImpl implements ReservationSeatService {

    private ReservationSeatRepository reservationSeatRepository;
    private SeatRepository seatRepository;

    public ReservationSeatServiceImpl(ReservationSeatRepository reservationSeatRepository, SeatRepository seatRepository) {
        this.reservationSeatRepository = reservationSeatRepository;
        this.seatRepository = seatRepository;
    }

    @Override
    public ReservationSeatAndroidDTO getReservationSeatByIdAndIdEvent(Long id, Long idEvent) {
        ReservationSeat reservationSeat = reservationSeatRepository.getReservationSeatByIdAndIdEvent(id, idEvent);
        if (reservationSeat == null) {
            return null;
        }
        Seat seat = reservationSeat.getSeat();
        return new ReservationSeatAndroidDTO(reservationSeat.getEventId(), seat.getSeatNumber(), seat.getRowNumber());
    }

    public List<ReservationWithSeatInfoDTO> getReservationSeatsByIdEvent(Long idEvent) {
        return reservationSeatRepository.getReservationByIdEvent(idEvent).stream().map(reservationSeat -> {
                    Reservation reservation = reservationSeat.getReservation();
                    Seat seat = reservationSeat.getSeat();
                    return new ReservationWithSeatInfoDTO(reservation.getId(), reservationSeat.getId(), seat.getId(), seat.getSeatNumber(), seat.getRowNumber(), reservation.getUserId(), reservation.getEventId());
                }
        ).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean updateReservationSeat(ReservationSeatCommand reservationSeatCommand) {
        try {
            int count = reservationSeatRepository.updateEvent(reservationSeatCommand.getId(), reservationSeatCommand.getSeatId());
            if (count >= 1) {
                Optional<ReservationSeat> reservationSeatOptional = reservationSeatRepository.findById(reservationSeatCommand.getId());
                Optional<Seat> seatOptional = seatRepository.findById(reservationSeatCommand.getSeatId());
                if (reservationSeatOptional.isPresent() && seatOptional.isPresent()) {
                    ReservationSeat reservationSeat = reservationSeatOptional.get();
                    Event event = reservationSeat.getEventReservation();
                    Seat seat = seatOptional.get();
                    if (Objects.equals(event.getIdCinemaHall(), seat.getCinemaHall().getId()) && seat.getSeatNumber() != null) {
                        return true;
                    }
                }
            }
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        } catch (Exception e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }
}
