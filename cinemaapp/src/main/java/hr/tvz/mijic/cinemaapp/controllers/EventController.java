package hr.tvz.mijic.cinemaapp.controllers;

import hr.tvz.mijic.cinemaapp.DTOs.EventCinemaDTO;
import hr.tvz.mijic.cinemaapp.DTOs.EventDTO;
import hr.tvz.mijic.cinemaapp.commands.EventCommand;
import hr.tvz.mijic.cinemaapp.commands.EventUpdateCommand;
import hr.tvz.mijic.cinemaapp.services.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("events")
@CrossOrigin(origins = "http://localhost:4200")
@Validated
public class EventController {

    private EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/active")
    public List<EventCinemaDTO> getActiveEventsById(Long idMovie) {
        return eventService.getActiveEventsById(idMovie);
    }

    @GetMapping("/30daysEvents")
    public ResponseEntity<Map<String, Object>> findEventsInNext30Days() throws ParseException {
        Map<String, Object> events30Days = this.eventService.findEventsInNext30Days();
        return new ResponseEntity<>(events30Days, HttpStatus.OK);
    }

    @GetMapping("movie-and-cinema-hall/{id}")
    public ResponseEntity<HashMap<String, Object>> getEventWithMovieAndCinemaHallById(@PathVariable String id) {
        HashMap<String, Object> eventWithMovieAndCinemaHall = eventService.getEventWithMovieAndCinemaHall(Long.valueOf(id));
        if (eventWithMovieAndCinemaHall == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return new ResponseEntity<>(eventWithMovieAndCinemaHall, HttpStatus.OK);
        }
    }

    @GetMapping("with-movie-cinema-hall-and-reservations/{id}")
    public ResponseEntity<HashMap<String, Object>> getEventWithMovieCinemaHallAndReservations(@PathVariable String id) {
        HashMap<String, Object> eventWithMovieCinemaHallAndReservations = eventService.getEventWithMovieCinemaHallAndReservations(Long.valueOf(id));
        if (eventWithMovieCinemaHallAndReservations == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return new ResponseEntity<>(eventWithMovieCinemaHallAndReservations, HttpStatus.OK);
        }
    }

    @GetMapping(value = "specificEvents", params = "idMovie")
    public ResponseEntity<List<EventCinemaDTO>> getEventsByIdMovie(@RequestParam String idMovie) {
        List<EventCinemaDTO> eventCinemaDTOList = eventService.getEventsByIdMovie(Long.valueOf(idMovie));
        return new ResponseEntity<>(eventCinemaDTOList, HttpStatus.OK);
    }

    @GetMapping("/admin/{idMovie}")
    public ResponseEntity<List<EventCinemaDTO>> getEventsByIdMovieAdmin(@PathVariable String idMovie) {
        List<EventCinemaDTO> eventCinemaDTOList = eventService.getEventsByIdMovieAdmin(Long.valueOf(idMovie));
        return new ResponseEntity<>(eventCinemaDTOList, HttpStatus.OK);
    }

    @GetMapping("/admin/specificEvent/{id}")
    public ResponseEntity<EventDTO> getEventByIdAdmin(@PathVariable Long id) {
        EventDTO eventDTO = eventService.getEventByIdAdmin(id);
        if (eventDTO == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(eventDTO, HttpStatus.OK);
        }
    }

    @PostMapping
    public ResponseEntity<EventDTO> addEvent(@Valid @RequestBody EventCommand eventCommand) {
        EventDTO eventDTO = eventService.addEvent(eventCommand);
        if (eventDTO == null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(eventDTO, HttpStatus.OK);
    }

    @PostMapping("addAll")
    public ResponseEntity<List<EventDTO>> addEvents(@RequestBody List<@Valid EventCommand> eventCommandList) {
        List<EventDTO> eventDTOList = eventService.addEvents(eventCommandList);
        if (eventDTOList.isEmpty() && eventDTOList == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return new ResponseEntity<>(eventDTOList, HttpStatus.OK);
    }


    @PutMapping("update")
    public ResponseEntity<EventDTO> updateEvent(@Valid @RequestBody EventUpdateCommand eventUpdateCommand) {
        boolean success = eventService.updateEvent(eventUpdateCommand);
        if (success) {
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping(params = "id")
    public ResponseEntity<EventDTO> deleteEvent(@RequestParam String id) {
        this.eventService.deleteEvent(Long.valueOf(id));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
