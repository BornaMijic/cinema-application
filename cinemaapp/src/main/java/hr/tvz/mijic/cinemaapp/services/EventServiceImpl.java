package hr.tvz.mijic.cinemaapp.services;

import hr.tvz.mijic.cinemaapp.DTOs.*;
import hr.tvz.mijic.cinemaapp.commands.EventCommand;
import hr.tvz.mijic.cinemaapp.commands.EventUpdateCommand;
import hr.tvz.mijic.cinemaapp.entities.*;
import hr.tvz.mijic.cinemaapp.repositories.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public List<EventCinemaDTO> getActiveEventsById(Long idMovie) {
        return eventRepository.getActiveEventsById(idMovie).stream().map(event -> new EventCinemaDTO(event.getId(), event.getDate(), event.getPrice(), event.getIdCinemaHall(), event.getCinemaHall().getName())).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> findEventsInNext30Days() {
        Instant now = Instant.now();
        ZoneId utc = ZoneId.of("UTC");
        ZonedDateTime currentDate = ZonedDateTime.ofInstant(now, utc);
        ZonedDateTime endDate = currentDate.plusDays(30);
        Map<String, Object> events30Days = new HashMap<>();
        events30Days.put("currentDate", currentDate);
        List<EventMovieDTO> eventMovieDTOList = eventRepository.findEventsInNext30Days().stream().map(e -> {
            Movie movie = e.getMovie();
            MovieDTO movieDTO = getMovieDTO(movie);
            return new EventMovieDTO(new EventDateDTO(e.getId(), e.getDate()), movieDTO);
        }).collect(Collectors.toList());
        events30Days.put("eventAndMovie", eventMovieDTOList);
        return events30Days;
    }

    @Override
    public HashMap<String, Object> getEventWithMovieAndCinemaHall(Long id) {
        Event event = eventRepository.findByIdForUser(id);
        if (event != null) {
            Movie movie = event.getMovie();
            MovieDTO movieDTO = getMovieDTO(movie);
            CinemaHall cinemaHall = event.getCinemaHall();
            return getHashMap(event, cinemaHall, movieDTO);
        }
        return null;
    }

    @Override
    public HashMap<String, Object> getEventWithMovieCinemaHallAndReservations(Long id) {
        Optional<Event> eventOptional = eventRepository.findByIdEventReservationManagment(id);
        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            Movie movie = event.getMovie();
            CinemaHall cinemaHall = event.getCinemaHall();
            List<ReservationSeat> reservationList = event.getReservationSeatList();
            MovieDTO movieDTO = getMovieDTO(movie);
            HashMap<String, Object> eventWithMovieCinemaHallAndReservations = getHashMap(event, cinemaHall, movieDTO);
            List<ReservationWithSeatInfoAndFullNameDTO> reservationWithSeatInfoAndFullNameDTOS = reservationList.stream().map(reservationSeat -> {
                User user = reservationSeat.getReservation().getUser();
                Seat seat = reservationSeat.getSeat();
                Reservation reservation = reservationSeat.getReservation();
                if (user == null) {
                    return new ReservationWithSeatInfoAndFullNameDTO(reservation.getId(), null, null, null, reservationSeat.getId(), seat.getId(), seat.getSeatNumber(), seat.getRowNumber());
                }
                return new ReservationWithSeatInfoAndFullNameDTO(reservation.getId(), user.getId(), user.getName(), user.getSurname(), reservationSeat.getId(), seat.getId(), seat.getSeatNumber(), seat.getRowNumber());
            }).collect(Collectors.toList());
            eventWithMovieCinemaHallAndReservations.put("reservationWithSeatInfoList", reservationWithSeatInfoAndFullNameDTOS);

            return eventWithMovieCinemaHallAndReservations;
        }
        return null;
    }

    @Override
    public List<EventCinemaDTO> getEventsByIdMovie(Long idMovie) {
        List<Event> eventList = eventRepository.findByIdMovie(idMovie);
        return eventList.stream().map(event -> new EventCinemaDTO(event.getId(), event.getDate(), event.getPrice(), event.getCinemaHall().getName())).collect(Collectors.toList());

    }

    @Override
    public List<EventCinemaDTO> getEventsByIdMovieAdmin(Long idMovie) {
        List<Event> eventList = eventRepository.findByIdMovieAdmin(idMovie);
        return eventList.stream().map(event -> new EventCinemaDTO(event.getId(), event.getDate(), event.getPrice(), event.getCinemaHall().getName())).collect(Collectors.toList());

    }

    @Override
    public EventDTO getEventByIdAdmin(Long id) {
        Optional<Event> eventOptional = eventRepository.findByIdEvent(id);
        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            return new EventDTO(event.getId(), event.getDate(), event.getPrice(), event.getIdMovie(), event.getIdCinemaHall());
        } else {
            return null;
        }
    }

    @Override
    @Transactional
    public EventDTO addEvent(EventCommand eventCommand) {
        try {
            Event event = eventRepository.save(new Event(eventCommand.getDate(), eventCommand.getPrice(), eventCommand.getIdMovie(), eventCommand.getIdCinemaHall()));
            return new EventDTO(event.getId(), event.getDate(), event.getPrice(), event.getIdMovie(), event.getIdCinemaHall());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    @Transactional
    public List<EventDTO> addEvents(List<EventCommand> eventCommandList) {
        try {
            List<Event> eventListJpa = eventCommandList.stream().map(eventCommand -> {
                return new Event(eventCommand.getDate(), eventCommand.getPrice(), eventCommand.getIdMovie(), eventCommand.getIdCinemaHall());
            }).collect(Collectors.toList());
            List<Event> eventList = eventRepository.saveAll(eventListJpa);
            return eventList.stream().map(event -> new EventDTO(event.getId(), event.getDate(), event.getPrice(), event.getIdMovie(), event.getIdCinemaHall())).collect(Collectors.toList());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    @Transactional
    public boolean updateEvent(EventUpdateCommand eventUpdateCommand) {
        int success = eventRepository.updateEvent(eventUpdateCommand.getId(), eventUpdateCommand.getDate(), eventUpdateCommand.getPrice());
        if (success >= 1) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    private MovieDTO getMovieDTO(Movie movie) {
        try {
            String posterName = movie.getPosterName();
            if (posterName.length() >= 4) {
                String fileType = posterName.substring(posterName.length() - 4) == "jpg" ? "jpeg" : "png";
                InputStream inputImage = new FileInputStream("src/main/resources/movies/" + movie.getId() + "-" + posterName);
                byte[] image = inputImage.readAllBytes();
                inputImage.close();
                return new MovieDTO(movie.getId(), movie.getTitle(), fileType, Base64.getEncoder().encodeToString(image), movie.getSummary(), movie.getDuration());
            } else {
                return new MovieDTO(movie.getId(), movie.getTitle(), "", "", movie.getSummary(), movie.getDuration());
            }
        } catch (IOException ioException) {
            return new MovieDTO(movie.getId(), movie.getTitle(), "", "", movie.getSummary(), movie.getDuration());
        }
    }

    private HashMap<String, Object> getHashMap(Event event, CinemaHall cinemaHall, MovieDTO movieDTO) {
        HashMap<String, Object> eventWithMovieAndCinemaHall = new HashMap<>();
        eventWithMovieAndCinemaHall.put("event", new EventDTO(event.getId(), event.getDate(), event.getPrice(), event.getIdMovie(), event.getIdCinemaHall()));
        eventWithMovieAndCinemaHall.put("movie", movieDTO);
        eventWithMovieAndCinemaHall.put("cinemaHall", new CinemaHallDTO(cinemaHall.getName(), cinemaHall.getGridRowsNumber(), cinemaHall.getGridColumnsNumber(), cinemaHall.getSeats().stream().map(seat -> new SeatDTO(seat.getId(), seat.getSeatNumber(), seat.getRowNumber(), seat.getGridPosition())).collect(Collectors.toList())));
        return eventWithMovieAndCinemaHall;
    }

}
