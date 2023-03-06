package hr.tvz.mijic.cinemaapp.controllers;

import hr.tvz.mijic.cinemaapp.DTOs.*;
import hr.tvz.mijic.cinemaapp.commands.*;
import hr.tvz.mijic.cinemaapp.services.EmailService;
import hr.tvz.mijic.cinemaapp.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Objects;

@RestController
@RequestMapping("api")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class UserController {

    private UserService userService;

    private AuthenticationManager authenticationManager;

    private EmailService emailService;

    public UserController(UserService userService, AuthenticationManager authenticationManager, EmailService emailService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<NameAndSurnameDTO> getUserById(@PathVariable Long id) {
        NameAndSurnameDTO user = userService.getUserById(id);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<NameAndSurnameDTO>(user, HttpStatus.OK);
    }

    @GetMapping("/users/page/admin/{pageNumber}")
    public ResponseEntity<HashMap<String, Object>> findFiveMoviesPerPageAdmin(@PathVariable int pageNumber) {
        HashMap<String, Object> pageUserHashMap = userService.findTenUsersPerPageAdmin(pageNumber);
        return new ResponseEntity<>(pageUserHashMap, HttpStatus.OK);
    }

    @GetMapping("/users/page/admin/{pageNumber}/{searchText}")
    public ResponseEntity<HashMap<String, Object>> findFiveMoviesPerPageWhichContainsSearchTextAdmin(@PathVariable int pageNumber, @PathVariable String searchText) {
        HashMap<String, Object> pageUserHashMap = userService.findTenUsersPerPageWhichContainsSearchUsernameAdmin(pageNumber, searchText);
        return new ResponseEntity<>(pageUserHashMap, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponseDTO> signup(@Valid @RequestBody RegistrationUserCommand registrationUserCommand) {
        if (!registrationUserCommand.getPassword().equals(registrationUserCommand.getConfirmPassword())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        RegistrationResponseAndVerificationDTO registrationResponseAndVerificationDTO = userService.register(registrationUserCommand);
        if (registrationResponseAndVerificationDTO == null || registrationResponseAndVerificationDTO.getRegistrationResponse() == null) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(registrationResponseAndVerificationDTO.getRegistrationResponse(), HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginDTO> login(@Valid @RequestBody UserCommand userCommand) {
        Authentication authentication = null;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(userCommand.getUsername(),
                            userCommand.getPassword()));
        } catch (Exception e) {
            return new ResponseEntity<LoginDTO>(HttpStatus.UNAUTHORIZED);
        }
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            LoginDTO loginDTO = this.userService.login(userCommand);
            if (loginDTO.isVerified() == false) {
                return new ResponseEntity<>(loginDTO, HttpStatus.OK);
            } else {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                return new ResponseEntity<>(loginDTO, HttpStatus.OK);
            }
        }
    }

    @PostMapping("/admin/login")
    public ResponseEntity<JwtDTO> adminLogin(@Valid @RequestBody UserCommand userCommand) {
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userCommand.getUsername(), userCommand.getPassword()));
        } catch (Exception e) {
            return new ResponseEntity<JwtDTO>(HttpStatus.UNAUTHORIZED);
        }
        JwtDTO jwtDTO = this.userService.adminLogin(userCommand);
        if (jwtDTO == null) {
            return new ResponseEntity<JwtDTO>(HttpStatus.UNAUTHORIZED);
        } else {
            return new ResponseEntity<JwtDTO>(jwtDTO, HttpStatus.OK);
        }
    }

    @PutMapping("/verify")
    public ResponseEntity<HashMap<String, Object>> verifyUser(@Valid @RequestBody VerificationCommand verificationCommand) throws Exception {
        String success = this.userService.verifyUser(verificationCommand);
        HashMap<String, Object> response = new HashMap<>();
        response.put("text", success);
        if (Objects.equals(success, "Verifikacija je bila uspješna")) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/change-email")
    public ResponseEntity<RegistrationResponseAndVerificationDTO> changeEmailAndSendVerificationLink(@Valid @RequestBody EmailChangeCommand emailChangeCommand) throws Exception {
        RegistrationResponseAndVerificationDTO response = this.userService.changeEmail(emailChangeCommand);
        if (response != null) {
            if (response.getRegistrationResponse() == null && response.getCodeToVerifyEmail() != null) {
                emailService.sendMessageForVerifyingEmail(emailChangeCommand.getEmail(), emailChangeCommand.getUsername(), "http://localhost:4200", response.getCodeToVerifyEmail());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }

        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/validate/{token}")
    public ResponseEntity<UserDTO> validateJWT(@PathVariable String token) {
        try {
            UserDTO userDTO = userService.validatingTokenAndGettingUser(token);
            if (userDTO == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            } else {
                return new ResponseEntity<>(userDTO, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/user/admin/active")
    public ResponseEntity<Object> changeActive(@RequestBody UserActiveCommand userActiveCommand) {
        boolean success = userService.changeActive(userActiveCommand);
        if (success == false) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }


    @PutMapping("/user/admin/verified")
    public ResponseEntity<Object> changeVerified(@RequestBody UserVerifiedCommand userVerifiedCommand) {
        boolean success = userService.changeVerified(userVerifiedCommand);
        if (success == false) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }


}
