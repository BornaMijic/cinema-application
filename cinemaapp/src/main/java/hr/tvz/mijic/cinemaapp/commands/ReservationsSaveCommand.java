package hr.tvz.mijic.cinemaapp.commands;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@Validated
public class ReservationsSaveCommand {

    @NotNull
    private Long userId;

    @NotNull
    private Long eventId;

    private List<ReservationSeatSaveCommand> reservationSeats;
}
