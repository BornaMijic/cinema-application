package hr.tvz.mijic.cinemaapp.controllers;

import hr.tvz.mijic.cinemaapp.DTOs.ReservationDTO;
import hr.tvz.mijic.cinemaapp.DTOs.ReservationUncompleteWithMovieEventAndCinemaHallDTO;
import hr.tvz.mijic.cinemaapp.DTOs.ReservationWithSeatInfoAndFullNameDTO;
import hr.tvz.mijic.cinemaapp.DTOs.ReservationWithSeatInfoDTO;
import hr.tvz.mijic.cinemaapp.commands.ReservationCommand;
import hr.tvz.mijic.cinemaapp.commands.ReservationSeatSaveCommand;
import hr.tvz.mijic.cinemaapp.commands.ReservationsSaveCommand;
import hr.tvz.mijic.cinemaapp.services.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("reservations")
@CrossOrigin(origins = "http://localhost:4200")
public class ReservationController {

    private ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping(params = "idEvent")
    public ResponseEntity<List<ReservationWithSeatInfoDTO>> getReservationsByEventId(@RequestParam String idEvent) {
        List<ReservationWithSeatInfoDTO> reservationWithSeatInfoDTOList = reservationService.getReservationsByIdEvent(Long.valueOf(idEvent));
        return new ResponseEntity<>(reservationWithSeatInfoDTOList, HttpStatus.OK);
    }

    @GetMapping("/currentReservation/uncomplete/{idUser}/{idEvent}")
    public ResponseEntity<HashMap<String, Object>> getReservationByIdEventAndIdUserAndUncomplete(@PathVariable Long idUser, @PathVariable Long idEvent) {
        HashMap<String, Object> reservationAndReservationSeats = this.reservationService.getReservationByIdEventAndIdUserAndUncomplete(idUser, idEvent);
        if (reservationAndReservationSeats == null) {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        return new ResponseEntity<>(reservationAndReservationSeats, HttpStatus.OK);
    }

    @GetMapping("/user/all/uncomplete/{idUser}")
    public ResponseEntity<List<ReservationUncompleteWithMovieEventAndCinemaHallDTO>> getAllReservationByIdUserWhichAreUncomplete(@PathVariable Long idUser) {
        List<ReservationUncompleteWithMovieEventAndCinemaHallDTO> reservationUncompleteWithMovieEventAndCinemaHallDTOList = this.reservationService.getAllReservationByIdUserWhichAreUncomplete(idUser);
        return new ResponseEntity<>(reservationUncompleteWithMovieEventAndCinemaHallDTOList, HttpStatus.OK);
    }

    @GetMapping("/user/count/uncomplete/{idUser}")
    public HashMap<String, Long> getCountOfUncompleteReservations(@PathVariable Long idUser) {
        HashMap<String, Long> countHashMap = this.reservationService.getCountOfUncompleteReservations(idUser);
        return countHashMap;
    }


    @GetMapping("/my-reservations/page/{pageNumber}/{idUser}")
    public ResponseEntity<HashMap<String, Object>> getReservationsByUserId(@PathVariable Long idUser, @PathVariable int pageNumber) {
        HashMap<String, Object> reservationsWithSeatEventMovieAndCinemaHallList = reservationService.getReservationsByIdUser(idUser, pageNumber);
        if (reservationsWithSeatEventMovieAndCinemaHallList == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return new ResponseEntity<>(reservationsWithSeatEventMovieAndCinemaHallList, HttpStatus.OK);
        }
    }


    @PostMapping
    public ResponseEntity<List<ReservationWithSeatInfoAndFullNameDTO>> addReservation(@Valid @RequestBody ReservationCommand reservationCommand) {
        List<ReservationWithSeatInfoAndFullNameDTO> reservationWithSeatInfoAndFullNameDTOList = reservationService.addReservation(reservationCommand);
        if (reservationWithSeatInfoAndFullNameDTOList == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        if (reservationWithSeatInfoAndFullNameDTOList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        }
        return new ResponseEntity<>(reservationWithSeatInfoAndFullNameDTOList, HttpStatus.OK);
    }

    @PostMapping("save-all")
    public ResponseEntity<ReservationDTO> addReservations(@Valid @RequestBody ReservationsSaveCommand reservationsSaveCommand, @RequestParam("eventDateString") String eventDateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .withZone(ZoneId.of("UTC"));
        LocalDateTime eventDate = LocalDateTime.parse(eventDateString, formatter);

        Instant currentTime = Instant.now();
        ZoneId utc = ZoneId.of("UTC");
        LocalDateTime currentUTCTime = LocalDateTime.ofInstant(currentTime, utc);

        if (currentUTCTime.isAfter(eventDate)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (reservationsSaveCommand.getReservationSeats() == null) {
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
        if (reservationsSaveCommand.getReservationSeats().isEmpty()) {
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator reservationSeatSaveValidator = factory.getValidator();
        int sumOfErrors = 0;
        for (ReservationSeatSaveCommand reservationSeatSaveCommand : reservationsSaveCommand.getReservationSeats()) {
            Set<ConstraintViolation<ReservationSeatSaveCommand>> errorCount = reservationSeatSaveValidator.validate(reservationSeatSaveCommand);
            sumOfErrors += errorCount.size();
        }

        if (sumOfErrors > 0) {
            return new ResponseEntity(HttpStatus.CONFLICT);
        } else {
            ReservationDTO reservationDTO = reservationService.addReservations(reservationsSaveCommand);
            if (reservationDTO == null) {
                return new ResponseEntity(HttpStatus.CONFLICT);
            }

            return new ResponseEntity<>(reservationDTO, HttpStatus.OK);
        }
    }

    @PutMapping("/confirm-reservation")
    public ResponseEntity<ReservationDTO> confirmReservation(@NotNull @RequestBody Long id) {
        boolean success = reservationService.confirmReservation(id);
        if (success) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/user/{id}/{idUser}")
    public ResponseEntity<Void> deleteByIdAndUserIdIfCompleteFalse(@PathVariable Long id, @PathVariable Long idUser) {
        reservationService.deleteByIdAndUserIdIfCompleteFalse(id, idUser);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/admin/{id}/{idReservationSeat}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id, @PathVariable Long idReservationSeat) {
        boolean success = reservationService.deleteReservation(id, idReservationSeat);
        if(success) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
}
