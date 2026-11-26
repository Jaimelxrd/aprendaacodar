package mz.co.aprendaacodar.config;

import mz.co.aprendaacodar.common.enums.Enums.UserRole;
import mz.co.aprendaacodar.user.entity.User;
import mz.co.aprendaacodar.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminBootstrap implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;

    public AdminBootstrap(
            UserRepository users,
            PasswordEncoder passwordEncoder,
            @Value("${app.bootstrap-admin.first-name:}") String firstName,
            @Value("${app.bootstrap-admin.last-name:}") String lastName,
            @Value("${app.bootstrap-admin.email:}") String email,
            @Value("${app.bootstrap-admin.password:}") String password) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (users.existsByRole(UserRole.ADMIN)) {
            log.info("Admin bootstrap skipped because an admin account already exists.");
            return;
        }

        if (firstName.isBlank() && lastName.isBlank() && email.isBlank() && password.isBlank()) {
            log.info("Admin bootstrap is disabled; no bootstrap credentials were configured.");
            return;
        }

        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank()) {
            throw new IllegalStateException("Configure APP_ADMIN_FIRST_NAME, APP_ADMIN_LAST_NAME, APP_ADMIN_EMAIL and APP_ADMIN_PASSWORD together.");
        }
        if (password.length() < 12) {
            throw new IllegalStateException("APP_ADMIN_PASSWORD must contain at least 12 characters.");
        }
        if (users.existsByEmailIgnoreCase(email.trim())) {
            throw new IllegalStateException("The configured bootstrap admin email is already used by a non-admin account.");
        }

        User admin = new User(firstName.trim(), lastName.trim(), email.trim(), null,
                passwordEncoder.encode(password), UserRole.ADMIN);
        admin.setEmailVerified(true);
        users.save(admin);
        log.info("Initial admin account created from bootstrap configuration.");
    }
}
