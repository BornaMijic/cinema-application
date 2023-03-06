package hr.tvz.mijic.cinemaapp.commands;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class UserVerifiedCommand {
    @NotNull
    private Long id;

    @NotBlank
    private String username;

    @NotNull
    private boolean verified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
