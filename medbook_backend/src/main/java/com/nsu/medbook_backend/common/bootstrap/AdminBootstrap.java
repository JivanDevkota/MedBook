package com.nsu.medbook_backend.common.bootstrap;

import com.nsu.medbook_backend.user.entity.Role;
import com.nsu.medbook_backend.user.entity.User;
import com.nsu.medbook_backend.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

/**
 * Seeds the first ADMIN account from configuration so a fresh installation
 * can reach the admin module. Enabled only when {@code app.bootstrap.admin.email}
 * is set; keep it unset in production and provision admins manually.
 */
@Component
@ConditionalOnProperty(name = "app.bootstrap.admin.email")
@Slf4j
public class AdminBootstrap implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String password;

    public AdminBootstrap(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.bootstrap.admin.email}") String email,
            @Value("${app.bootstrap.admin.password}") String password
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.email = email;
        this.password = password;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmailIgnoreCase(normalized)) {
            return;
        }
        userRepository.save(User.builder()
                .username(normalized)
                .email(normalized)
                .password(passwordEncoder.encode(password))
                .firstName("System")
                .lastName("Admin")
                .role(Role.ADMIN)
                .active(true)
                .build());
        log.warn("Seeded bootstrap admin account {}. Disable app.bootstrap.admin once real admins exist.", normalized);
    }
}
