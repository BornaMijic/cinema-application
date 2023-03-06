package hr.tvz.mijic.cinemaapp.DTOs;

public class UserUpdateDTO {
    private Long id;
    private String username;
    private String role;
    private boolean verified;
    private boolean active;

    public UserUpdateDTO(Long id, String username, String role, boolean verified, boolean active) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.verified = verified;
        this.active = active;
    }

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
