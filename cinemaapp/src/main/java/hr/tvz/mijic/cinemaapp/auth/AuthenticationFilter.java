package hr.tvz.mijic.cinemaapp.auth;

import hr.tvz.mijic.cinemaapp.DTOs.UsernameWithRole;
import hr.tvz.mijic.cinemaapp.services.UserService;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    private UserService userService;

    @Value("${jwt.secret}")
    private String secret;

    public AuthenticationFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        String method = request.getMethod();
        if ((method.equals("GET") && !"/api/users".contains(path) && !path.contains("movies/admin/page/") && !path.contains("events/admin/")
                && !path.contains("cinema-halls/admin/page/") && !path.contains("/movies/active") && !path.contains("/events/active/")
                && !path.contains("/reservation-seats/getSpecificReservationSeat/") && !path.contains("/reservations/currentReservation/uncomplete/")
                && !path.contains("/reservations/user/all/uncomplete/") && !path.contains("/reservations/user/count/uncomplete/")
                && !path.contains("/reservations/my-reservations/page/") && !path.contains("/events/admin/specificEvent")
                && !path.contains("/movies/admin/specificMovie/"))
                || "/api/verify".contains(path) || "/api/change-email".contains(path) || "/mail/verification-email".contains(path)
                || path.contains("/api/login") || path.contains("/api/register") || path.contains("/api/admin/login") || "/h2-console".contains(path)
                || (method.equals("DELETE") && List.of("/reservations").contains(path))) {
            filterChain.doFilter(request, response);
            return;
        }

        if (request.getHeader(AUTHORIZATION) != null) {
            String header = request.getHeader(AUTHORIZATION).substring(6);
            if (userService.isTokenValid(header)) {
                String id = null;
                try {
                    id = Jwts.parser().setSigningKey(secret).parseClaimsJws(header).getBody().getSubject();
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
                if (id != null) {
                    UsernameWithRole usernameWithRole = userService.getRolesById(id);
                    String roleName = usernameWithRole.getRoles().get(0).getAuthority();
                    if (usernameWithRole.getRoles() == null || usernameWithRole.getRoles().isEmpty() || usernameWithRole.getUsername() == null) {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    } else {
                        if ((Objects.equals(roleName, "ROLE_USER") || Objects.equals(roleName, "ROLE_ADMIN")) && (
                                (method.equals("DELETE") && path.contains("/reservations/user/")) ||
                                        (method.equals("POST") && (path.contains("/reservations/save-all") || path.contains("/payment/order")
                                                || path.contains("/payment/approve") || path.contains("/mail/export") || path.contains("/pdf/export")))
                                        || (method.equals("GET") && (path.contains("/reservations/currentReservation/uncomplete/")
                                        || path.contains("/reservations/user/all/uncomplete/") || path.contains("/reservations/user/count/uncomplete/")
                                        || path.contains("/reservations/my-reservations/page/")))
                        )) {
                            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(usernameWithRole.getUsername(), null, usernameWithRole.getRoles());
                            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                            }
                            filterChain.doFilter(request, response);
                            return;
                        }
                        if (Objects.equals(roleName, "ROLE_ADMIN")) {
                            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(usernameWithRole.getUsername(), null, usernameWithRole.getRoles());
                            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                            }
                            filterChain.doFilter(request, response);
                            return;
                        }
                    }
                }
            }
        }

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        return;
    }
}
