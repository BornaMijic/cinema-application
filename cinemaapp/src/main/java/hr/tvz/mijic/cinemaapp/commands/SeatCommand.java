package hr.tvz.mijic.cinemaapp.commands;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SeatCommand {
    private Integer seatNumber;

    @NotNull
    private int rowNumber;

    @NotNull
    private int gridPosition;
}
