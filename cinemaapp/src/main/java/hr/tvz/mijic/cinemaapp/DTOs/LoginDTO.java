package hr.tvz.mijic.cinemaapp.DTOs;

import lombok.Data;

@Data
public class LoginDTO {
    private String username;
    private String email;
    private String token;
    private boolean verified;
    private String codeToVerifyEmail;
    private boolean active;

    public LoginDTO() {
    }

    public LoginDTO( String username, String email, String token,boolean verified, String codeToVerifyEmail, boolean active) {
        this.username = username;
        this.email = email;
        this.token = token;
        this.verified = verified;
        this.codeToVerifyEmail = codeToVerifyEmail;
        this.active = active;
    }

}
