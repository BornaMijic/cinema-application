package hr.tvz.mijic.cinemaapp.commands;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class VerificationCommand {
    @NotBlank
    @Size(min=5, max=50)
    private String username;

    @NotBlank
    private String codeToVerifyAccount;
}
