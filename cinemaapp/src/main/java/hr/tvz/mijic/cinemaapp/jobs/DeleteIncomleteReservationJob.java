package hr.tvz.mijic.cinemaapp.jobs;

import hr.tvz.mijic.cinemaapp.entities.Reservation;
import hr.tvz.mijic.cinemaapp.repositories.ReservationRepository;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.List;

public class DeleteIncomleteReservationJob implements Job {

    private final ReservationRepository reservationRepository;

    public DeleteIncomleteReservationJob(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        List<Reservation> reservations = reservationRepository.getIncompleteReservation();
        if (!reservations.isEmpty()) {
            reservations.stream().forEach((reservation -> reservationRepository.deleteById(reservation.getId())));
        }
    }
}
