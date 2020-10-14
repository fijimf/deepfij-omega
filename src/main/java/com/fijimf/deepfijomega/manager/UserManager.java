package com.fijimf.deepfijomega.manager;

import com.fijimf.deepfijomega.entity.user.AuthToken;
import com.fijimf.deepfijomega.entity.user.Role;
import com.fijimf.deepfijomega.entity.user.User;
import com.fijimf.deepfijomega.repository.AuthTokenRepository;
import com.fijimf.deepfijomega.repository.RoleRepository;
import com.fijimf.deepfijomega.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserManager implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthTokenRepository authTokenRepository;

    private final PasswordEncoder passwordEncoder;
    public static final String EMAIL_REGEX = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    public static final Predicate<String> emailMatches = Pattern.compile(EMAIL_REGEX).asMatchPredicate();

    @Autowired
    public UserManager(UserRepository userRepository, RoleRepository roleRepository, AuthTokenRepository authTokenRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authTokenRepository = authTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<User> ou = userRepository.findFirstByUsername(s);
        if (ou.isEmpty()) {
            throw new UsernameNotFoundException("Could not fine uer " + s);
        } else {
            return ou.get();
        }
    }

    public String createNewUser(String username, String password, String email, List<String> roles) {
        if (StringUtils.isBlank(username)) throw new IllegalArgumentException("username must not be null or blank.");
        if (StringUtils.isBlank(password)) throw new IllegalArgumentException("password must not be null or blank.");
        if (StringUtils.isBlank(email)) throw new IllegalArgumentException("email must not be null or blank.");
        if (!emailMatches.test(email)) throw new IllegalArgumentException("email '"+email+"' is malformed");
        if (roles == null || roles.isEmpty()) throw new IllegalArgumentException("roles must not be null or empty.");
        User user = new User(username, passwordEncoder.encode(password), email);
        user.setActivated(false);
        user.setLocked(false);
        if (userRepository.findFirstByEmail(email).isEmpty()) {
            if (userRepository.findFirstByUsername(username).isEmpty()) {
                List<Role> rs = roles.stream().flatMap(r -> roleRepository.findFirstByRole(r).stream()).collect(Collectors.toList());
                if (!rs.isEmpty()) {
                    user.setRoles(rs);
                    User u = userRepository.save(user);
                    AuthToken auth = new AuthToken(u.getId(), RandomStringUtils.randomAlphanumeric(12), LocalDateTime.now().plusHours(3));
                    authTokenRepository.save(auth);
                    return auth.getToken();
                } else {
                    throw new RuntimeException("No roles found for new user");
                }
            } else {
                throw new DuplicatedUsernameException(username);
            }
        } else {
            throw new DuplicatedEmailException(email);
        }
    }

    public void activateUser(String token) {
        Optional<AuthToken> ot = authTokenRepository.findByToken(token);
        if (ot.isPresent()) {
            if (ot.get().getExpiresAt().isAfter(LocalDateTime.now())) {
                Optional<User> ou = userRepository.findById(ot.get().getUserId());
                if (ou.isPresent()) {
                    User user = ou.get();
                    if (!user.isActivated()) {
                        user.setActivated(true);
                        userRepository.save(user);
                    }
                    authTokenRepository.deleteById(ot.get().getId());
                }
            } else {
                authTokenRepository.deleteById(ot.get().getId());
            }
        }
    }


    //createUser
    //activateUser
    //lockUser
    //changePassword
    //tempCredentials

}
