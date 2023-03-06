package hr.tvz.mijic.cinemaapp.services;

import hr.tvz.mijic.cinemaapp.DTOs.*;
import hr.tvz.mijic.cinemaapp.commands.*;

import java.util.HashMap;

public interface UserService {
    NameAndSurnameDTO getUserById(Long id);

    RegistrationResponseAndVerificationDTO register(RegistrationUserCommand registrationUserCommand);

    String verifyUser(VerificationCommand verificationCommand) throws Exception;

    LoginDTO login(UserCommand userCommand);

    RegistrationResponseAndVerificationDTO changeEmail(EmailChangeCommand emailChangeCommand) throws Exception;

    JwtDTO adminLogin(UserCommand userCommand);

    UsernameWithRole getRolesById(String id);

    UserDTO validatingTokenAndGettingUser(String token) throws Exception;

    boolean isTokenValid(String token);

    HashMap<String, Object> findTenUsersPerPageAdmin(int pageNumber);

    HashMap<String, Object> findTenUsersPerPageWhichContainsSearchUsernameAdmin(int pageNumber, String searchUsername);

    boolean changeActive(UserActiveCommand userActiveCommand);

    boolean changeVerified(UserVerifiedCommand userVerifiedCommand);

}
