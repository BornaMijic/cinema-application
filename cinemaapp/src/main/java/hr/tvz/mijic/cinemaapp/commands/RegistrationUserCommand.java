package hr.tvz.mijic.cinemaapp.commands;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class RegistrationUserCommand {
    @NotBlank
    @Size(min=6, max=30)
    @Pattern(regexp = "^[a-zA-Z0-9]+$")
    private String username;

    @NotBlank
    @Size(min=6, max=30)
    @Pattern(regexp = "^[a-zA-Z0-9]+$")
    private String password;

    @NotBlank
    @Size(min=6, max=30)
    @Pattern(regexp = "^[a-zA-Z0-9]+$")
    private String confirmPassword;


    @NotBlank
    @Size(min=1, max=60)
    @Pattern(regexp = "^[a-zA-Z]+$")
    private String name;

    @NotBlank
    @Size(min=1, max=60)
    @Pattern(regexp = "^[a-zA-Z]+$")
    private String surname;

    @NotBlank
    @Size(min=5, max=60)
    @Email
    @Pattern(regexp = "[0-9a-z._]+@gmail.com$")
    private String email;
}
