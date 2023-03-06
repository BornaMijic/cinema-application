package hr.tvz.mijic.cinemaapp.commands;

import lombok.Data;

import javax.validation.constraints.*;
import java.util.List;

@Data
public class CinemaHallCommand {

    @NotBlank
    private String name;

    @NotNull
    @Min(value = 1)
    @Max(value = 20)
    private Integer rows;

    @NotNull
    @Min(value = 1)
    @Max(value = 20)
    private Integer columns;

    private List<SeatCommand> seats;
}
