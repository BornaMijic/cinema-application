package hr.tvz.mijic.cinemaapp.commands;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CaptureRequestCommand {

    @NotBlank
    private String idOrder;

    @NotNull
    private Long idReservation;

    @NotNull
    private Long idUser;

    private EmailCommand EmailInfo;
}
