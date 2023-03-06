package hr.tvz.mijic.cinemaapp.DTOs;

import lombok.Data;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Data
public class UsernameWithRole {
    private String username;
    private List<SimpleGrantedAuthority> roles;

    public UsernameWithRole(String username, List<SimpleGrantedAuthority> roles) {
        this.username = username;
        this.roles = roles;
    }
}
