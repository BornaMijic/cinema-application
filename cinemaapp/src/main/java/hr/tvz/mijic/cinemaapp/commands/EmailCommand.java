package hr.tvz.mijic.cinemaapp.commands;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class EmailCommand {

    @NotBlank
    private String userEmail;

    @NotBlank
    private String cinemaHallName;

    @NotBlank
    private String title;

    @NotBlank
    private String dateString;

    @NotBlank
    private String hourString;

    @NotNull
    private Double price;

    private List<UserReservationCommand> userReservationSeats;

    public EmailCommand(String userEmail, String cinemaHallName, String title, String dateString, String hourString, Double price, List<UserReservationCommand> userReservationSeats) {
        this.userEmail = userEmail;
        this.cinemaHallName = cinemaHallName;
        this.title = title;
        this.dateString = dateString;
        this.hourString = hourString;
        this.price = price;
        this.userReservationSeats = userReservationSeats;
    }
}
