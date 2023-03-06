package hr.tvz.mijic.cinemaapp.commands;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserCommand {

    @NotBlank
    @Size(min=5, max=50)
    private String username;

    @NotBlank
    @Size(min=5, max=50)
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
