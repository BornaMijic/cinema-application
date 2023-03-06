package hr.tvz.mijic.cinemaapp.services;

import hr.tvz.mijic.cinemaapp.DTOs.*;
import hr.tvz.mijic.cinemaapp.commands.ReservationCommand;
import hr.tvz.mijic.cinemaapp.commands.ReservationsSaveCommand;
import hr.tvz.mijic.cinemaapp.entities.*;
import hr.tvz.mijic.cinemaapp.repositories.ReservationRepository;
import hr.tvz.mijic.cinemaapp.repositories.ReservationSeatRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ReservationServiceImpl implements ReservationService {

    private ReservationRepository reservationRepository;
    private ReservationSeatRepository reservationSeatRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public ReservationServiceImpl(ReservationRepository reservationRepository, ReservationSeatRepository reservationSeatRepository) {
        this.reservationRepository = reservationRepository;
        this.reservationSeatRepository = reservationSeatRepository;
    }

    @Override
    public List<ReservationWithSeatInfoDTO> getReservationsByIdEvent(Long idEvent) {
        List<ReservationWithSeatInfoDTO> reservationWithSeatInfoDTOList = new ArrayList<>();
        reservationRepository.getReservationByIdEvent(idEvent).stream().forEach(reservation -> {
            for (ReservationSeat reservationSeat : reservation.getReservationSeats()) {
                Seat seat = reservationSeat.getSeat();
                reservationWithSeatInfoDTOList.add(new ReservationWithSeatInfoDTO(reservation.getId(), reservationSeat.getId(), seat.getId(), seat.getSeatNumber(), seat.getRowNumber(), reservation.getUserId(), reservation.getEventId()));

            }

        });
        return reservationWithSeatInfoDTOList;
    }

    @Override
    public HashMap<String, Object> getReservationByIdEventAndIdUserAndUncomplete(Long idUser, Long idEvent) {
        Reservation reservation = this.reservationRepository.getReservationByIdEventAndIdUserAndUncomplete(idUser, idEvent);
        if (reservation != null) {
            HashMap<String, Object> reservationAndReservationSeats = new HashMap<>();
            reservationAndReservationSeats.put("idReservation", reservation.getId());
            reservationAndReservationSeats.put("reservationTime", reservation.getReservationTime());
            List<ReservationSeatDTO> reservationSeatDTOList = reservation.getReservationSeats().stream().map(reservationSeat -> {
                Seat seat = reservationSeat.getSeat();
                return new ReservationSeatDTO(reservationSeat.getId(), reservationSeat.getSeatId(), seat.getSeatNumber(), seat.getRowNumber());
            }).collect(Collectors.toList());
            reservationAndReservationSeats.put("reservationSeats", reservationSeatDTOList);
            return reservationAndReservationSeats;
        } else {
            return null;
        }
    }

    @Override
    public List<ReservationUncompleteWithMovieEventAndCinemaHallDTO> getAllReservationByIdUserWhichAreUncomplete(Long idUser) {
        List<Reservation> reservationList = this.reservationRepository.getAllReservationByIdUserWhichAreUncomplete(idUser);
        return reservationList.stream().map(reservation -> {
            Event event = reservation.getEvent();
            CinemaHall cinemaHall = event.getCinemaHall();
            Movie movie = event.getMovie();
            return new ReservationUncompleteWithMovieEventAndCinemaHallDTO(
                    reservation.getId(),
                    reservation.getReservationTime(),
                    new MovieDTO(movie.getId(), movie.getTitle(), "", "", movie.getSummary(), movie.getDuration()),
                    new EventDTO(event.getId(), event.getDate(), event.getPrice(), event.getIdMovie(), event.getIdCinemaHall()),
                    new MultipleCinemaHallsDTO(cinemaHall.getId(), cinemaHall.getName()),
                    reservation.getReservationSeats().stream().map(reservationSeat -> {
                        Seat seat = reservationSeat.getSeat();
                        return new ReservationSeatDTO(reservationSeat.getId(), reservationSeat.getSeatId(), seat.getSeatNumber(), seat.getRowNumber());
                    }).collect(Collectors.toList()));
        }).collect(Collectors.toList());
    }


    @Override
    public HashMap<String, Long> getCountOfUncompleteReservations(Long idUser) {
        Long count = this.reservationRepository.getCountOfUncompleteReservations(idUser);
        HashMap<String, Long> countHashMap = new HashMap<>();
        countHashMap.put("countUncompleteReservations", count);
        return countHashMap;
    }


    @Override
    public HashMap<String, Object> getReservationsByIdUser(Long idUser, int pageNumber) {
        Pageable requestingPageWithFiveUserReservation = PageRequest.of(pageNumber, 5);
        Page<Reservation> reservationList = reservationRepository.getReservationByIdUser(idUser, requestingPageWithFiveUserReservation);
        List<ReservationsWithSeatEventMovieAndCinemaHallDTO> reservationsWithSeatEventMovieAndCinemaHallDTOList = reservationList.stream().map(reservation -> {
            Event event = reservation.getEvent();
            CinemaHall cinemaHall = event.getCinemaHall();
            Movie movie = event.getMovie();

            List<ReservationSeatDTO> reservationSeatDTOS = reservation.getReservationSeats().stream().map(
                    reservationSeat -> {
                        Seat seat = reservationSeat.getSeat();
                        return new ReservationSeatDTO(reservationSeat.getId(), seat.getId(), seat.getSeatNumber(), seat.getRowNumber());
                    }).collect(Collectors.toList());
            return new ReservationsWithSeatEventMovieAndCinemaHallDTO(reservation.getId(),
                    reservationSeatDTOS,
                    new EventDTO(event.getId(), event.getDate(), event.getPrice(), event.getIdMovie(), event.getIdCinemaHall()),
                    new MovieDTO(movie.getId(), movie.getTitle(), "", "", movie.getSummary(), movie.getDuration()),
                    new MultipleCinemaHallsDTO(cinemaHall.getId(), cinemaHall.getName())
            );
        }).collect(Collectors.toList());
        HashMap<String, Object> hashMapReservations = new HashMap<>();
        hashMapReservations.put("count", reservationList.getTotalElements());
        hashMapReservations.put("reservationsWithSeatEventMovieAndCinemaHall", reservationsWithSeatEventMovieAndCinemaHallDTOList);

        return hashMapReservations;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public List<ReservationWithSeatInfoAndFullNameDTO> addReservation(ReservationCommand reservationCommand) {
        try {
            Reservation reservation = null;
            Instant currentTime = Instant.now();
            ZoneId utc = ZoneId.of("UTC");
            LocalDateTime reservationTime = LocalDateTime.ofInstant(currentTime, utc);
            Reservation reservationSave = new Reservation();
            reservationSave.setReservationTime(reservationTime);
            reservationSave.setEventId(reservationCommand.getEventId());
            List<ReservationSeat> reservationSeatList = new ArrayList<>();
            ReservationSeat reservationSeat = new ReservationSeat();
            reservationSeat.setSeatId(reservationCommand.getSeatId());
            reservationSeat.setEventId(reservationCommand.getEventId());
            reservationSeatList.add(reservationSeat);
            reservationSave.setReservationSeats(reservationSeatList);
            reservationSave.setComplete(true);
            reservation = this.reservationRepository.saveAndFlush(reservationSave);
            entityManager.refresh(reservation);
            return getReservationWithSeatInfoAndFullNameDTO(reservation);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ReservationDTO addReservations(ReservationsSaveCommand reservationsSaveCommand) {
        try {
            Instant currentTime = Instant.now();
            ZoneId utc = ZoneId.of("UTC");
            LocalDateTime reservationTime = LocalDateTime.ofInstant(currentTime, utc);
            Reservation reservationSave = new Reservation(reservationTime, reservationsSaveCommand.getUserId(), reservationsSaveCommand.getEventId(), false);
            reservationsSaveCommand.getReservationSeats().stream().forEach(reservationSeatSaveCommand -> reservationSave.addReservationSeats(new ReservationSeat(reservationSeatSaveCommand.getSeatId(), reservationSeatSaveCommand.getEventId())));
            Reservation reservation = this.reservationRepository.saveAndFlush(reservationSave);
            entityManager.refresh(reservation);
            ReservationDTO reservationDTO = new ReservationDTO(reservation.getId(), reservation.getUserId());
            List<ReservationSeatDTO> reservationDTOList = reservation.getReservationSeats().stream().map(reservationSeat -> {
                Seat seat = reservationSeat.getSeat();
                return new ReservationSeatDTO(reservationSeat.getId(), seat.getId(), seat.getSeatNumber(), seat.getRowNumber());
            }).collect(Collectors.toList());
            reservationDTO.setReservationSeats(reservationDTOList);
            return reservationDTO;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    private List<ReservationWithSeatInfoAndFullNameDTO> getReservationWithSeatInfoAndFullNameDTO(Reservation reservation) {
        List<ReservationSeat> reservationSeatList = reservation.getReservationSeats();
        try {
            List<ReservationWithSeatInfoAndFullNameDTO> reservationWithSeatInfoAndFullNameDTOList = reservationSeatList.stream().map(reservationSeat -> {
                Seat seat = reservationSeat.getSeat();
                User user = reservation.getUser();
                Event event = reservationSeat.getEventReservation();
                if (!Objects.equals(seat.getCinemaHall().getId(), event.getIdCinemaHall())) {
                    TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                    throw new IllegalArgumentException();
                }
                if (seat.getSeatNumber() == null) {
                    TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                    throw new IllegalArgumentException();
                }

                if (user == null) {
                    return new ReservationWithSeatInfoAndFullNameDTO(reservation.getId(), null, null, null, reservationSeat.getId(), seat.getId(), seat.getSeatNumber(), seat.getRowNumber());
                }
                return new ReservationWithSeatInfoAndFullNameDTO(reservation.getId(), reservation.getUserId(), user.getName(), user.getSurname(), reservationSeat.getId(), seat.getId(), seat.getSeatNumber(), seat.getRowNumber());
            }).collect(Collectors.toList());
            return reservationWithSeatInfoAndFullNameDTOList;
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    @Transactional
    public boolean confirmReservation(Long id) {
        int count = reservationRepository.confirmReservation(id);
        if (count == 1) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void deleteByIdAndUserIdIfCompleteFalse(Long id, Long idUser) {
        this.reservationRepository.deleteByIdAndUserIdIfCompleteFalse(id, idUser);
    }

    @Override
    @Transactional
    public void deleteByIdAndUserId(Long id, Long idUser) {
        this.reservationRepository.deleteByIdAndUserId(id, idUser);
    }


    @Override
    public boolean deleteReservation(Long id, Long idReservationSeat) {
        Reservation reservation = reservationRepository.getReservationById(id);
        if (reservation != null) {
            if (reservation.getReservationSeats().size() <= 1) {
                reservationRepository.deleteById(id);
            } else {
                ReservationSeat reservationSeat = reservation.getReservationSeats().stream().filter(reservationSeatItem -> Objects.equals(reservationSeatItem.getId(), idReservationSeat)).collect(Collectors.toList()).get(0);
                reservation.removeReservationSeat(reservationSeat);
                reservationRepository.save(reservation);

            }
            return true;
        } else {
            return false;
        }
    }
}
