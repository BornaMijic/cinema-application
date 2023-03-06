package hr.tvz.mijic.cinemaapp.commands;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class VerificationEmailInfoCommand {
    @NotBlank
    @Size(min=5, max=60)
    @Email
    private String email;

    @NotBlank
    @Size(min=5, max=50)
    private String username;

    @NotBlank
    private String link;

    @NotBlank
    private String codeToVerifyEmail;
}
