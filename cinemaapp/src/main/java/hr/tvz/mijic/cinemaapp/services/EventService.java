package hr.tvz.mijic.cinemaapp.services;

import hr.tvz.mijic.cinemaapp.DTOs.EventCinemaDTO;
import hr.tvz.mijic.cinemaapp.DTOs.EventDTO;
import hr.tvz.mijic.cinemaapp.commands.EventCommand;
import hr.tvz.mijic.cinemaapp.commands.EventUpdateCommand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface EventService {
    List<EventCinemaDTO> getActiveEventsById(Long idMovie);

    Map<String, Object> findEventsInNext30Days();

    HashMap<String, Object> getEventWithMovieAndCinemaHall(Long id);

    HashMap<String, Object> getEventWithMovieCinemaHallAndReservations(Long id);

    List<EventCinemaDTO> getEventsByIdMovie(Long idMovie);

    List<EventCinemaDTO> getEventsByIdMovieAdmin(Long idMovie);

    EventDTO getEventByIdAdmin(Long id);

    EventDTO addEvent(EventCommand eventCommand);

    List<EventDTO> addEvents(List<EventCommand> eventCommandList);

    boolean updateEvent(EventUpdateCommand eventUpdateCommand);

    void deleteEvent(Long id);

}
