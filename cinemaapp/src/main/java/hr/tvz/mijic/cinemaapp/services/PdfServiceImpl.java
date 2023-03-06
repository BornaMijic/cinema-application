package hr.tvz.mijic.cinemaapp.services;

import hr.tvz.mijic.cinemaapp.DTOs.PdfDTO;
import hr.tvz.mijic.cinemaapp.commands.PdfCommand;
import hr.tvz.mijic.cinemaapp.commands.UserReservationCommand;
import hr.tvz.mijic.cinemaapp.entities.ReservationSeat;
import hr.tvz.mijic.cinemaapp.repositories.PdfGeneration;
import hr.tvz.mijic.cinemaapp.repositories.ReservationSeatRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

@Service
public class PdfServiceImpl implements PdfService {

    private PdfGeneration pdfGeneration;
    private ReservationSeatRepository reservationSeatRepository;


    public PdfServiceImpl(PdfGeneration pdfGeneration, ReservationSeatRepository reservationSeatRepository) {
        this.pdfGeneration = pdfGeneration;
        this.reservationSeatRepository = reservationSeatRepository;
    }

    @Override
    public PdfDTO generatePdf(PdfCommand tickets, Long idUser) throws IOException {
        Boolean succes = true;
        for (UserReservationCommand userReservationCommand : tickets.getUserReservationSeats()) {
            Optional<ReservationSeat> reservationSeatOptional = reservationSeatRepository.findById(userReservationCommand.getIdReservationSeat());
            if (reservationSeatOptional.isEmpty()) {
                succes = false;
                break;
            }
            if (reservationSeatOptional.get().getReservation().getUserId() != idUser) {
                succes = false;
                break;
            }
        }

        if (succes == true) {
            return new PdfDTO(Base64.getEncoder().encodeToString(pdfGeneration.generatePdf(tickets)));
        } else {
            return null;
        }
    }


    @Override
    public PdfDTO generatePdfAdmin(PdfCommand tickets) throws IOException {
        return new PdfDTO(Base64.getEncoder().encodeToString(pdfGeneration.generatePdf(tickets)));

    }
}
