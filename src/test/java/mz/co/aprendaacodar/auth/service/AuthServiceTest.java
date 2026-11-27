package mz.co.aprendaacodar.auth.service;

import mz.co.aprendaacodar.auth.dto.AuthDtos.LoginRequest;
import mz.co.aprendaacodar.common.enums.Enums.UserRole;
import mz.co.aprendaacodar.common.enums.Enums.UserStatus;
import mz.co.aprendaacodar.security.JwtService;
import mz.co.aprendaacodar.security.RefreshTokenRepository;
import mz.co.aprendaacodar.user.entity.User;
import mz.co.aprendaacodar.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AuthServiceTest {
    private UserRepository users;
    private PasswordEncoder passwords;
    private AuthService service;

    @BeforeEach
    void setUp() {
        users = mock(UserRepository.class);
        passwords = mock(PasswordEncoder.class);
        service = new AuthService(users, mock(RefreshTokenRepository.class), passwords, mock(JwtService.class), 1000L);
    }

    @Test
    void loginRejectsUnknownEmail() {
        when(users.findByEmailIgnoreCase("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class,
                () -> service.login(new LoginRequest("missing@example.com", "some-password")));
        verifyNoInteractions(passwords);
    }

    @Test
    void loginRejectsIncorrectPassword() {
        User user = new User("Test", "Student", "student@example.com", null, "bcrypt-hash", UserRole.STUDENT);
        when(users.findByEmailIgnoreCase("student@example.com")).thenReturn(Optional.of(user));
        when(passwords.matches("wrong-password", "bcrypt-hash")).thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> service.login(new LoginRequest("student@example.com", "wrong-password")));
    }

    @Test
    void loginRejectsBlockedAccountBeforePasswordCheck() {
        User user = new User("Blocked", "Student", "blocked@example.com", null, "bcrypt-hash", UserRole.STUDENT);
        user.setStatus(UserStatus.BLOCKED);
        when(users.findByEmailIgnoreCase("blocked@example.com")).thenReturn(Optional.of(user));

        assertThrows(BadCredentialsException.class,
                () -> service.login(new LoginRequest("blocked@example.com", "some-password")));
        verifyNoInteractions(passwords);
    }
}
