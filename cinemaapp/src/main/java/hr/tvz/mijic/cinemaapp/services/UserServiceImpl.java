package hr.tvz.mijic.cinemaapp.services;

import hr.tvz.mijic.cinemaapp.DTOs.*;
import hr.tvz.mijic.cinemaapp.commands.*;
import hr.tvz.mijic.cinemaapp.entities.Role;
import hr.tvz.mijic.cinemaapp.entities.User;
import hr.tvz.mijic.cinemaapp.repositories.RoleRepository;
import hr.tvz.mijic.cinemaapp.repositories.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private UserRepository userRepository;

    private RoleRepository roleRepository;

    @Value("${jwt.secret}")
    private String secret;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public NameAndSurnameDTO getUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return new NameAndSurnameDTO(user.getId(), user.getName(), user.getSurname());
        } else {
            return null;
        }
    }

    @Override
    @Transactional
    public RegistrationResponseAndVerificationDTO register(RegistrationUserCommand registrationUserCommand) {
        String email = registrationUserCommand.getEmail();
        if (!userRepository.findByUsername(registrationUserCommand.getUsername()).isEmpty()) {
            return new RegistrationResponseAndVerificationDTO(new RegistrationResponseDTO("Korisničko ime je zauzeto"),
                    null);
        } else if (!userRepository.findByEmail(email).isEmpty()) {
            return new RegistrationResponseAndVerificationDTO(new RegistrationResponseDTO("Email je zauzet"),
                    null);
        } else {
            User user;
            Set<Role> roles = new HashSet<>();
            Optional<Role> roleOptional = roleRepository.findByName("ROLE_USER");
            if (roleOptional.isPresent()) {
                Role role = roleOptional.get();
                role.setName(role.getName());
                roles.add(role);
                String codeToVerify = RandomString.make(255);
                user = new User(registrationUserCommand.getUsername(),
                        BCrypt.hashpw(registrationUserCommand.getPassword(), BCrypt.gensalt()),
                        registrationUserCommand.getName().substring(0, 1).toUpperCase() +
                                registrationUserCommand.getName().substring(1),
                        registrationUserCommand.getSurname().substring(0, 1).toUpperCase() +
                                registrationUserCommand.getSurname().substring(1),
                        email, codeToVerify, false, true, role.getId());
            } else {
                return new RegistrationResponseAndVerificationDTO(new RegistrationResponseDTO("Došlo je do pogreške"), null);
            }

            try {
                userRepository.save(user);
                return new RegistrationResponseAndVerificationDTO(null, user.getCodeToVerify());
            } catch (Exception e) {
                return new RegistrationResponseAndVerificationDTO(new RegistrationResponseDTO("Došlo je do pogreške"), null);
            }
        }
    }

    @Override
    @Transactional
    public String verifyUser(VerificationCommand verificationCommand) {
        int count = userRepository.verifyUser(verificationCommand.getUsername(), verificationCommand.getCodeToVerifyAccount());
        System.out.println(verificationCommand.getUsername());
        if (count == 1) {
            return "Verifikacija je bila uspješna";
        } else {
            User user = userRepository.findIfAlreadyVerified(verificationCommand.getUsername());
            if (user != null) {
                return "Korisnički račun je već verificiran";
            } else {
                return "Verifikacija nije bila uspješna";
            }
        }
    }

    @Override
    @Transactional
    public LoginDTO login(UserCommand userCommand) {
        Optional<User> userOptional = userRepository.findByUsername(userCommand.getUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Role role = user.getRole();
            String token = createJwtToken(user, role);
            return setLoginDTORole(user, token, role, user.isVerified(), user.getCodeToVerify(), user.isActive());
        }
        return null;
    }

    @Override
    @Transactional
    public RegistrationResponseAndVerificationDTO changeEmail(EmailChangeCommand emailChangeCommand) {
        String newCodeToVerify = RandomString.make(255);
        User firstUser = userRepository.isVerified(emailChangeCommand.getUsername(), emailChangeCommand.getCodeToVerifyEmail());
        boolean verified = firstUser.isVerified();
        if (verified) {
            return new RegistrationResponseAndVerificationDTO(new RegistrationResponseDTO("Korisnik je verificiran, ne možete promjeniti email"), null);
        }
        if (!userRepository.findByEmail(emailChangeCommand.getEmail()).isEmpty()) {
            return new RegistrationResponseAndVerificationDTO(new RegistrationResponseDTO("Email je zauzet"), null);
        }
        int count = 0;

        try {
            count = userRepository.changeEmail(emailChangeCommand.getUsername(), emailChangeCommand.getCodeToVerifyEmail(), emailChangeCommand.getEmail(), newCodeToVerify);
        } catch (Exception ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return null;
        }

        User user = userRepository.isVerified(emailChangeCommand.getUsername(), newCodeToVerify);
        verified = user.isVerified();

        if (user == null) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return null;
        }

        if (verified) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return null;
        }
        if (count == 1) {
            return new RegistrationResponseAndVerificationDTO(null, newCodeToVerify);
        } else if (count > 1) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return null;
        } else {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }

    @Override
    @Transactional
    public JwtDTO adminLogin(UserCommand userCommand) {
        Optional<User> userOptional = userRepository.findByUsername(userCommand.getUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String token = createJwtToken(user, user.getRole());
            if (user.getRole().getName().equals("ROLE_ADMIN")) {
                return new JwtDTO(token);
            } else {
                return null;
            }
        }
        return null;
    }

    @Override
    @Transactional
    public UsernameWithRole getRolesById(String id) {
        Optional<User> userOptional = userRepository.findById(Long.valueOf(id));
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Role role = user.getRole();
            if (!user.isActive() || !user.isVerified()) {
                return null;
            }
            List<SimpleGrantedAuthority> roles = new ArrayList<>();
            roles.add(new SimpleGrantedAuthority(role.getName()));
            UsernameWithRole usernameWithRole = new UsernameWithRole(user.getUsername(), roles);

            return usernameWithRole;
        }
        return null;
    }

    @Override
    public UserDTO validatingTokenAndGettingUser(String token) {
        try {
            if (isTokenValid(token)) {
                Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
                Long id = Long.valueOf(getIdFromToken(token));
                Optional<User> userOptional = userRepository.findById(id);
                if (!userOptional.isEmpty()) {
                    User user = userOptional.get();
                    Role role = user.getRole();
                    return setUserDTORole(user, token, role);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private UserDTO setUserDTORole(User user, String token, Role role) {
        return new UserDTO(user.getUsername(), user.getEmail(), token, role.getName());
    }

    private LoginDTO setLoginDTORole(User user, String token, Role role, boolean verified, String codeToVerifyEmail, boolean active) {
        return new LoginDTO(user.getUsername(), user.getEmail(), token, verified, codeToVerifyEmail, active);
    }

    private String createJwtToken(User user, Role role) {
        Map<String, Object> claims = new HashMap<>();
        List<SimpleGrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(role.getName()));
        claims.put("roles", roles);
        return Jwts.builder().setClaims(claims)
                .setSubject(user.getId().toString())
                .setExpiration(new Date(System.currentTimeMillis() + (15 * 24 * 60 * 1000)))
                .signWith(SignatureAlgorithm.HS512, secret).compact();

    }

    public String getIdFromToken(final String token) {
        try {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
        } catch (Exception ex) {
            return null;
        }
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<SimpleGrantedAuthority> userRoles = new ArrayList<>();
            userRoles.add(new SimpleGrantedAuthority(user.getRole().getName()));
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), userRoles);
        }
        return null;
    }

    @Override
    public HashMap<String, Object> findTenUsersPerPageAdmin(int pageNumber) {
        Pageable requestingPageWithTenUser = PageRequest.of(pageNumber, 10);
        Page<User> usersPage = userRepository.findTenUserPerPageAdmin(requestingPageWithTenUser);
        HashMap<String, Object> pageUserHashMap = new HashMap<>();
        pageUserHashMap.put("usersAmount", usersPage.getTotalElements());
        pageUserHashMap.put("usersList", getUserUpdateDTOS(usersPage));
        return pageUserHashMap;
    }

    @Override
    public HashMap<String, Object> findTenUsersPerPageWhichContainsSearchUsernameAdmin(int pageNumber, String searchUsername) {
        Pageable pageWithTenUsers = PageRequest.of(pageNumber, 10);
        Page<User> usersPage = userRepository.findTenUserPerPageWhichContainsSearchUsernameAdmin(searchUsername, pageWithTenUsers);
        HashMap<String, Object> pageUserHashMap = new HashMap<>();
        pageUserHashMap.put("usersAmount", usersPage.getTotalElements());
        pageUserHashMap.put("usersList", getUserUpdateDTOS(usersPage));
        return pageUserHashMap;
    }

    private List<UserUpdateDTO> getUserUpdateDTOS(Page<User> usersPage) {
        return usersPage.stream().map(user -> {
            Role role = user.getRole();
            return new UserUpdateDTO(user.getId(), user.getUsername(), role.getName(), user.isVerified(), user.isActive());
        }).collect(Collectors.toList());
    }

    @Override
    public boolean changeActive(UserActiveCommand userActiveCommand) {
        int count = userRepository.changeActive(userActiveCommand.getId(), userActiveCommand.getUsername(), userActiveCommand.isActive());
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean changeVerified(UserVerifiedCommand userVerifiedCommand) {
        int count = userRepository.changeVerified(userVerifiedCommand.getId(), userVerifiedCommand.getUsername(), userVerifiedCommand.isVerified());
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

}
