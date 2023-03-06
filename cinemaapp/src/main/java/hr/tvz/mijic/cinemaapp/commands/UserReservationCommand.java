package hr.tvz.mijic.cinemaapp.commands;

import lombok.Data;

@Data
public class UserReservationCommand {
    private int rowNumber;
    private int seatNumber;
    private Long idReservationSeat;

    public UserReservationCommand(int rowNumber, int seatNumber, Long idReservationSeat) {
        this.rowNumber = rowNumber;
        this.seatNumber = seatNumber;
        this.idReservationSeat = idReservationSeat;
    }
}
