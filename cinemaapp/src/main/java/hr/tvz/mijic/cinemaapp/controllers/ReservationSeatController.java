package hr.tvz.mijic.cinemaapp.controllers;

import hr.tvz.mijic.cinemaapp.DTOs.ReservationSeatAndroidDTO;
import hr.tvz.mijic.cinemaapp.DTOs.ReservationWithSeatInfoDTO;
import hr.tvz.mijic.cinemaapp.commands.ReservationSeatCommand;
import hr.tvz.mijic.cinemaapp.services.ReservationSeatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("reservation-seats")
@CrossOrigin(origins = "http://localhost:4200")
public class ReservationSeatController {

    private ReservationSeatService reservationSeatService;

    public ReservationSeatController(ReservationSeatService reservationSeatService) {
        this.reservationSeatService = reservationSeatService;
    }

    @GetMapping("/getSpecificReservationSeat/{id}/{idEvent}")
    public ResponseEntity<ReservationSeatAndroidDTO> getReservationSeatByIdAndIdEvent(@PathVariable Long id, @PathVariable Long idEvent) {
        ReservationSeatAndroidDTO reservationSeatAndroidDTO = reservationSeatService.getReservationSeatByIdAndIdEvent(id, idEvent);
        if (reservationSeatAndroidDTO == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(reservationSeatAndroidDTO, HttpStatus.OK);
    }

    @GetMapping("/{idEvent}")
    public ResponseEntity<List<ReservationWithSeatInfoDTO>> getReservationsByEventId(@PathVariable String idEvent) {
        List<ReservationWithSeatInfoDTO> reservationWithSeatInfoDTOList = reservationSeatService.getReservationSeatsByIdEvent(Long.valueOf(idEvent));
        return new ResponseEntity<>(reservationWithSeatInfoDTOList, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Void> updateReservation(@Valid @RequestBody ReservationSeatCommand reservationSeatCommand) {
        boolean success = reservationSeatService.updateReservationSeat(reservationSeatCommand);
        if (success == false) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
