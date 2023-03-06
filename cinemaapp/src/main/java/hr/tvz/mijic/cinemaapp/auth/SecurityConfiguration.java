package hr.tvz.mijic.cinemaapp.auth;

import hr.tvz.mijic.cinemaapp.services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

    private AuthenticationFilter authenticationFilter;

    private UserServiceImpl userServiceImpl;

    private ErrorHandling errorHandling;

    public SecurityConfiguration(AuthenticationFilter authenticationFilter, UserServiceImpl userServiceImpl, ErrorHandling errorHandling) {
        this.authenticationFilter = authenticationFilter;
        this.userServiceImpl = userServiceImpl;
        this.errorHandling = errorHandling;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userServiceImpl).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors().and().csrf().disable().authorizeRequests()
                .antMatchers().permitAll()
                .antMatchers(HttpMethod.GET, new String[]{"/movies", "/cinema-halls",
                        "/api/validate/*", "/movies/specificMovie/*", "/movies/latest",
                        "/movies/soon-starting", "/events", "/events/specificEvent/*",
                        "/events/specificEvents/*", "/reservations", "/reservations/*",
                        "/cinema-halls/*", "/seats", "/movies/page/*", "/movies/page/*/*",
                        "/events/30daysEvents", "/events/movie-and-cinema-hall/*",
                        "/events/with-movie-cinema-hall-and-reservations/*", "/api/users/page/admin/*",
                        "/api/users/page/admin/*/*", "/api/user/*", "/movies/image/*",
                        "/seats/*/*", "/reservation-seats/getSpecificReservation/*/*"}).permitAll()
                .antMatchers(HttpMethod.POST, new String[]{"/api/login", "/api/register", "/api/admin/login",
                        "/mail/verification-email"}).permitAll()
                .antMatchers(HttpMethod.DELETE, new String[]{"/movies/*/*", "/movies/*", "/events/*",
                        "/reservations/admin/*/*", "/cinema-halls/*"}).access("hasRole('ROLE_ADMIN')")
                .antMatchers(HttpMethod.DELETE, new String[]{"/reservations/user/*/*"})
                .access("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
                .antMatchers(HttpMethod.POST, new String[]{"/movies", "/events",
                        "/events/addAll", "/cinema-halls", "/reservations/"})
                .access("hasRole('ROLE_ADMIN')")
                .antMatchers(HttpMethod.POST, new String[]{"/mail"}).permitAll()
                .antMatchers(HttpMethod.POST, new String[]{"/reservations/save-all",
                        "/payment/order", "/payment/approve", "/mail/export", "/pdf/export/*"})
                .access("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
                .antMatchers(HttpMethod.PUT, new String[]{"/api/change-email", "/api/verify"}).permitAll()
                .antMatchers(HttpMethod.PUT, new String[]{"/movies/update", "/movies/update/image",
                        "/events/update", "/reservation-seats", "/api/users/admin/active",
                        "/api/users/admin/verified"})
                .access("hasRole('ROLE_ADMIN')")
                .antMatchers(HttpMethod.GET,
                        new String[]{"/reservations/currentReservation/uncomplete/{idUser}/{idEvent}",
                                "/reservations/user/all/uncomplete/*", "/reservations/user/count/uncomplete/*",
                        "/reservations/my-reservations/page/*/*"})
                .access("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
                .antMatchers(HttpMethod.GET, new String[]{"/movies/admin/page/*", "/events/admin/*",
                        "/cinema-halls/admin/page/*", "/events/active/*", "/cinema-halls/admin/page/*/*",
                        "/movies/active", "/reservation-seats/getSpecificReservationSeat/*/*",
                        "/events/admin/specificEvent/*", "/movies/admin/specificMovie/*", "/pdf/admin/export"})
                .access("hasRole('ROLE_ADMIN')")
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(errorHandling)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        httpSecurity.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
