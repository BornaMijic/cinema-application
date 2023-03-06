package hr.tvz.mijic.cinemaapp.controllers;

import hr.tvz.mijic.cinemaapp.DTOs.SeatDTO;
import hr.tvz.mijic.cinemaapp.services.SeatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("seats")
@CrossOrigin(origins = "http://localhost:4200")
public class SeatController {

    private SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @GetMapping(params = "id")
    public ResponseEntity<SeatDTO> geSeatById(@RequestParam String id) {
        SeatDTO seatDTO = this.seatService.getSeatById(Long.valueOf(id));
        if (seatDTO != null) {
            return new ResponseEntity<>(seatDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
