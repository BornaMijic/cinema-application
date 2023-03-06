package hr.tvz.mijic.cinemaapp.commands;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class UserActiveCommand {
    @NotNull
    private Long id;

    @NotBlank
    private String username;

    @NotNull
    private boolean active;

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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
