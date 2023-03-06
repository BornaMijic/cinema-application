package hr.tvz.mijic.cinemaapp.controllers;

import hr.tvz.mijic.cinemaapp.DTOs.MultipleCinemaHallsDTO;
import hr.tvz.mijic.cinemaapp.commands.CinemaHallCommand;
import hr.tvz.mijic.cinemaapp.commands.SeatCommand;
import hr.tvz.mijic.cinemaapp.services.CinemaHallService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("cinema-halls")
@CrossOrigin(origins = "http://localhost:4200")
public class CinemaHallController {

    private CinemaHallService cinemaHallService;

    public CinemaHallController(CinemaHallService cinemaHallService) {
        this.cinemaHallService = cinemaHallService;
    }

    @GetMapping
    public ResponseEntity<List<MultipleCinemaHallsDTO>> getAllCinemaHall() {
        List<MultipleCinemaHallsDTO> multipleCinemaHallsDTOList = this.cinemaHallService.getAllCinemaHalls();
        return new ResponseEntity<>(multipleCinemaHallsDTOList, HttpStatus.OK);
    }

    @GetMapping("/admin/page/{pageNumber}")
    public ResponseEntity<Map<String, Object>> findTenCinemaHallsPerPage(@PathVariable int pageNumber) {
        Map<String, Object> pageCinemaHall = cinemaHallService.findTenCinemaHallsPerPage(pageNumber);
        return new ResponseEntity<>(pageCinemaHall, HttpStatus.OK);
    }


    @GetMapping("/admin/page/{pageNumber}/{searchText}")
    public ResponseEntity<Map<String, Object>> findTenCinemaHallsPerPageWhichContainsSearchText(@PathVariable int pageNumber, @PathVariable String searchText) {
        Map<String, Object> pageCinemaHall = cinemaHallService.findTenCinemaHallsPerPageWhichContainsSearchText(pageNumber, searchText);
        return new ResponseEntity<>(pageCinemaHall, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> addCinemaHall(@Valid @RequestBody CinemaHallCommand cinemaHallCommand) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator cinemaHallValidator = factory.getValidator();
        int sumOfErrors = 0;
        for (SeatCommand seatCommand : cinemaHallCommand.getSeats()) {
            Set<ConstraintViolation<SeatCommand>> errorCount = cinemaHallValidator.validate(seatCommand);
            sumOfErrors += errorCount.size();
        }

        if (sumOfErrors > 0) {
            return new ResponseEntity(HttpStatus.CONFLICT);
        } else {
            boolean success = this.cinemaHallService.addCinemaHall(cinemaHallCommand);
            if (success == true) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCinemaHallById(@PathVariable Long id) {
        this.cinemaHallService.deleteCinemaHallById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
