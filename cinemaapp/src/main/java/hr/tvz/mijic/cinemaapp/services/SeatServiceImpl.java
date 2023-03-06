package hr.tvz.mijic.cinemaapp.services;

import hr.tvz.mijic.cinemaapp.DTOs.SeatDTO;
import hr.tvz.mijic.cinemaapp.entities.Seat;
import hr.tvz.mijic.cinemaapp.repositories.SeatRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SeatServiceImpl implements SeatService {

    private SeatRepository seatRepository;

    public SeatServiceImpl(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    public SeatDTO getSeatById(Long id) {
        Optional<Seat> seatOptional = this.seatRepository.findById(id);
        if (seatOptional.isPresent()) {
            Seat seat = seatOptional.get();
            return new SeatDTO(seat.getId(), seat.getSeatNumber(), seat.getRowNumber(), seat.getGridPosition());
        } else {
            return null;
        }
    }

}
