package hr.tvz.mijic.cinemaapp.commands;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class EmailChangeCommand {

    @NotBlank
    @Size(min=5, max=50)
    private String username;

    @NotBlank
    @Size(min=1, max=60)
    private String email;

    @NotBlank
    private String link;

    @NotBlank
    private String codeToVerifyEmail;
}
