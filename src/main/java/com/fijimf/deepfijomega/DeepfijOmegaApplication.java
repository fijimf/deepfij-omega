package com.fijimf.deepfijomega;

import com.fijimf.deepfijomega.entity.user.User;
import com.fijimf.deepfijomega.mailer.Mailer;
import com.fijimf.deepfijomega.manager.UserManager;
import com.fijimf.deepfijomega.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class DeepfijOmegaApplication {
    public static final Logger logger = LoggerFactory.getLogger(DeepfijOmegaApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DeepfijOmegaApplication.class, args);

        UserManager userMgr = context.getBean(UserManager.class);
        UserRepository userRepository = context.getBean(UserRepository.class);
        PasswordEncoder passwordEncoder = context.getBean(PasswordEncoder.class);
        String password = RandomStringUtils.randomAlphabetic(6);
        Optional<User> ou = userRepository.findFirstByUsername("admin");
        if (ou.isPresent()) {
            User u = ou.get();
            u.setPassword(passwordEncoder.encode(password));
            u.setActivated(true);
            u.setLocked(false);
            u.setExpireCredentialsAt(LocalDateTime.now().plusMinutes(10));
            userRepository.save(u);
        } else {
            String token = userMgr.createNewUser("admin", password, "deepfij@gmail.com", List.of("USER", "ADMIN"), 10);
            userMgr.activateUser(token);
        }
        logger.info("admin password is {}", password);

        Mailer mailer = context.getBean(Mailer.class);
        try {
            mailer.sendStartupMessage(password);
        } catch (MessagingException | IOException e) {
            logger.error("Failed mailing server startup message", e);
        }

    }

}
