package hr.tvz.mijic.cinemaapp.repositories;

import hr.tvz.mijic.cinemaapp.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query(value = "SELECT * FROM users ORDER BY id DESC", countQuery = "SELECT COUNT(*) FROM users ORDER BY id DESC", nativeQuery = true)
    Page<User> findTenUserPerPageAdmin(Pageable pageable);

    @Query(value = "SELECT * FROM users WHERE LOWER(username) LIKE %:searchUsername% ORDER BY id DESC", countQuery = "SELECT COUNT(*) FROM users WHERE LOWER(username) LIKE %:searchUsername% ORDER BY id DESC", nativeQuery = true)
    Page<User> findTenUserPerPageWhichContainsSearchUsernameAdmin(@Param("searchUsername") String searchUsername, Pageable pageable);

    @Query(value = "SELECT * FROM users WHERE username LIKE :username AND code_to_verify LIKE :codeToVerifyEmail", nativeQuery = true)
    User isVerified(@Param("username") String username, @Param("codeToVerifyEmail") String codeToVerifyEmail);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE users SET verified = TRUE WHERE username LIKE :username AND code_to_verify LIKE :codeToVerifyEmail AND verified = FALSE", nativeQuery = true)
    int verifyUser(@Param("username") String username, @Param("codeToVerifyEmail") String codeToVerifyEmail);

    @Query(value = "SELECT * FROM users WHERE username LIKE :username AND verified = TRUE", nativeQuery = true)
    User findIfAlreadyVerified(@Param("username") String username);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE users SET email = :email, code_to_verify = :newCodeToVerifyEmail, verified = FALSE WHERE username LIKE :username AND code_to_verify LIKE :codeToVerifyEmail AND verified = FALSE", nativeQuery = true)
    int changeEmail(@Param("username") String username, @Param("codeToVerifyEmail") String codeToVerifyEmail, @Param("email") String email, @Param("newCodeToVerifyEmail") String newCodeToVerifyEmail);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE users INNER JOIN roles ON users.role_id = roles.id SET active = :active WHERE users.id = :id AND username LIKE :username AND active <> :active AND roles.name LIKE 'ROLE_USER'", nativeQuery = true)
    int changeActive(@Param("id") Long id, @Param("username") String username, @Param("active") boolean active);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE users INNER JOIN roles ON users.role_id = roles.id SET verified = :verified WHERE users.id = :id AND username LIKE :username AND verified <> :verified AND roles.name LIKE 'ROLE_USER'", nativeQuery = true)
    int changeVerified(@Param("id") Long id, @Param("username") String username, @Param("verified") boolean verified);
}
