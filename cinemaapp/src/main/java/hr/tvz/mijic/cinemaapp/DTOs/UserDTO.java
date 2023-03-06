package hr.tvz.mijic.cinemaapp.DTOs;

import lombok.Data;

@Data
public class UserDTO {
    private String username;
    private String email;
    private String token;
    private String role;

    public UserDTO() {
    }

    public UserDTO( String username, String email, String token, String role) {
        this.username = username;
        this.email = email;
        this.token = token;
        this.role = role;
    }

}
