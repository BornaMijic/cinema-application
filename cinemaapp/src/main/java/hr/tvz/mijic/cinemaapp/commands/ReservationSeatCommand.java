package hr.tvz.mijic.cinemaapp.commands;


import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ReservationSeatCommand {
    @NotNull
    private Long id;

    @NotNull
    private Long seatId;
}