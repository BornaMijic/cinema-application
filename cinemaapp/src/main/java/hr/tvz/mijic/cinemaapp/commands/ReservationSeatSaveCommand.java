package hr.tvz.mijic.cinemaapp.commands;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ReservationSeatSaveCommand {
    @NotNull
    private Long seatId;

    @NotNull
    private Long eventId;
}
