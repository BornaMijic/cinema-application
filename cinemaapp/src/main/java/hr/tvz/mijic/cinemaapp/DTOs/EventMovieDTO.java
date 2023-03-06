package hr.tvz.mijic.cinemaapp.DTOs;

import lombok.Data;

@Data
public class EventMovieDTO {
    private EventDateDTO event;
    private MovieDTO movie;

    public EventMovieDTO(EventDateDTO event, MovieDTO movie) {
        this.event = event;
        this.movie = movie;
    }
}
