package com.fijimf.deepfijomega.integration.manager;

import com.fijimf.deepfijomega.entity.user.User;
import com.fijimf.deepfijomega.integration.utility.DockerPostgresDb;
import com.fijimf.deepfijomega.manager.DuplicatedEmailException;
import com.fijimf.deepfijomega.manager.DuplicatedUsernameException;
import com.fijimf.deepfijomega.manager.UserManager;
import com.fijimf.deepfijomega.repository.AuthTokenRepository;
import com.fijimf.deepfijomega.repository.RoleRepository;
import com.fijimf.deepfijomega.repository.UserRepository;
import com.spotify.docker.client.exceptions.DockerException;
import org.apache.commons.text.RandomStringGenerator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DataJpaTest
@EntityScan(basePackages = {"com.fijimf.deepfijomega.entity"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class UserManagerTest {
    private static final DockerPostgresDb dockerDb = new DockerPostgresDb("postgres:13-alpine", 59432);

    @Autowired
    private UserRepository repository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AuthTokenRepository authTokenRepository;

    private final SecureRandom random = new SecureRandom();
    private final char[][] pairs = {{'a', 'z'}, {'A', 'Z'}};
    private final RandomStringGenerator rsg = new RandomStringGenerator.Builder()
            .usingRandom(random::nextInt)
            .withinRange(pairs)
            .build();


    @BeforeAll
    public static void spinUpDatabase() throws DockerException, InterruptedException {
        dockerDb.spinUpDatabase();
    }

    @AfterAll
    public static void spinDownDb() throws DockerException, InterruptedException {
        dockerDb.spinDownDb();
    }

    @Test
    public void createUserSuccess() {
        UserManager userManager = new UserManager(repository, roleRepository, authTokenRepository, new BCryptPasswordEncoder(), rsg);
        assertThat(repository.findAll()).isEmpty();
        String authCode = userManager.createNewUser("Jim", "zzz", "fijimf@yahoo.com", List.of("USER"));
        assertThat(authCode).isNotBlank();

        Optional<User> ffbe = repository.findFirstByEmail("fijimf@yahoo.com");
        assertThat(ffbe).isPresent();
        Optional<User> ffbu = repository.findFirstByUsername("Jim");
        assertThat(ffbu).isPresent();
        assertThat(ffbe).isEqualTo(ffbu);
        assertThat(ffbe).hasValueSatisfying(u -> {
            assertThat(u.getAuthorities()).hasSize(1);
            assertThat(u.getAuthorities()).allMatch(a -> a.getAuthority().equals("USER"));
            assertThat(u.getId()).isGreaterThan(0L);
            assertThat(u.getExpireCredentialsAt()).isNull();
            assertThat(u.isAccountNonExpired()).isTrue();
            assertThat(u.isAccountNonLocked()).isTrue();
            assertThat(u.isActivated()).isFalse();
            assertThat(u.getRoles()).hasSize(1);
            assertThat(u.getRoles()).allMatch(s -> s.getAuthority().equals("USER"));
        });
    }

    @Test
    public void createUserFailNullBlankArgument() {
        UserManager userManager = new UserManager(repository, roleRepository, authTokenRepository, new BCryptPasswordEncoder(), rsg);
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> userManager.createNewUser(null, "zzz", "fijimf@yahoo.com", List.of("USER")));
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> userManager.createNewUser("Jim", null, "fijimf@yahoo.com", List.of("USER")));
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> userManager.createNewUser("Jim", "zzz", null, List.of("USER")));
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> userManager.createNewUser("Jim", "zzz", "fijimf@yahoo.com", null));
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> userManager.createNewUser("", "zzz", "fijimf@yahoo.com", List.of("USER")));
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> userManager.createNewUser("Jim", "", "fijimf@yahoo.com", List.of("USER")));
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> userManager.createNewUser("Jim", "zzz", "", List.of("USER")));
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> userManager.createNewUser("Jim", "zzz", "fijimf@yahoo.com", List.of()));
    }

    @Test
    public void createUserFailIllegalArgument() {
        UserManager userManager = new UserManager(repository, roleRepository, authTokenRepository, new BCryptPasswordEncoder(), rsg);
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> userManager.createNewUser("Jim", "zzz", "fijimf$yahoo.com", List.of("USER")));
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> userManager.createNewUser("Jim", null, "fzzzzzzzzz", List.of("USER")));
    }

    @Test
    public void createUserFailDuplicate() {
        UserManager userManager = new UserManager(repository, roleRepository, authTokenRepository, new BCryptPasswordEncoder(), rsg);
        userManager.createNewUser("Jim", "zzz", "fijimf@yahoo.com", List.of("USER"));
        assertThatExceptionOfType(DuplicatedEmailException.class)
                .isThrownBy(() -> userManager.createNewUser("Tom", "zzz", "fijimf@yahoo.com", List.of("USER")));
        assertThatExceptionOfType(DuplicatedEmailException.class)
                .isThrownBy(() -> userManager.createNewUser("Jim", "zzz", "fijimf@yahoo.com", List.of("USER")));
        assertThatExceptionOfType(DuplicatedUsernameException.class)
                .isThrownBy(() -> userManager.createNewUser("Jim", "zzz", "fijimf@hotmail.com", List.of("USER")));
    }

    @Test
    public void testEmailRegex() {
        Predicate<String> p = Pattern.compile(UserManager.EMAIL_REGEX).asMatchPredicate();
        assertThat(p).rejects(
                "Salmon P. Chase",
                "@",
                "  @  .  com",
                ".username@yahoo.com",
                "username@yahoo.com.",
                "username@yahoo..com",
                "username@yahoo.c",
                "username@yahoo.corporate",
                "user#domain.com",
                "@yahoo.com",
                "user.name@domain.domain.domain.domain.domain.domain.domain.domain.domain.com",
                "user.name.name.name.name.name.name.name@domain.com"
        );
        assertThat(p).accepts(
                "user@domain.com",
                "user@domain.co.in",
                "user.name@domain.com",
                "user_name@domain.com",
                "usernameusernameusername@yahoo.yahoo.yahoo.corporate.in",
                "usernameusernameusername@yahooyahooyahoo.yahoo.yahoo.corporate.in",
                "username@yahoo.corporatecorporatecorporatecorporate.in",
                "username@yahoo.corporate.in"
        );
    }
}
