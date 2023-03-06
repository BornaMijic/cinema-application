package hr.tvz.mijic.cinemaapp.commands;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
public class EventCommand {

    @NotNull
    private LocalDateTime date;

    @NotNull
    @PositiveOrZero
    @Min(value = 0)
    @Max(value = 2147483647)
    @Digits(integer = 10, fraction = 2)
    private Double price;

    @NotNull
    private Long idMovie;

    @NotNull
    private Long idCinemaHall;

}
