package hr.tvz.mijic.cinemaapp.commands;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ReservationCommand {

    @NotNull
    private Long seatId;

    private Long userId;

    @NotNull
    private Long eventId;
}
