package hr.tvz.mijic.cinemaapp.repositories;

import hr.tvz.mijic.cinemaapp.entities.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    @Override
    Optional<Seat> findById(Long id);
}
