package com.fijimf.deepfijomega;

import com.fijimf.deepfijomega.entity.user.User;
import com.fijimf.deepfijomega.mailer.Mailer;
import com.fijimf.deepfijomega.manager.DuplicatedEmailException;
import com.fijimf.deepfijomega.manager.DuplicatedUsernameException;
import com.fijimf.deepfijomega.manager.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class UserController {

    public static final Logger logger = LoggerFactory.getLogger(UserController.class);
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final UserManager userManager;
    private final Mailer mailer;

    @Autowired
    public UserController(
            UserManager userManager, Mailer mailer) {
        this.userManager = userManager;
        this.mailer = mailer;
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute User user, Model model) {
        model.addAttribute("email", user.getEmail());
        model.addAttribute("username", user.getUsername());
        try {
            String authCode = userManager.createNewUser(user.getUsername(), user.getPassword(), user.getEmail(), List.of("USER"));
            mailer.sendAuthEmail(user.getUsername(), user.getEmail(), authCode);
            return "signupComplete";
        } catch (IllegalArgumentException ex) {
            logger.warn("Illegal argument creating user", ex);
            model.addAttribute("error", ex.getMessage());
            return "signup";
        } catch (DuplicatedEmailException ex) {
            logger.warn("", ex);
            model.addAttribute("error", ex.getMessage());
            return "signup";
        } catch (DuplicatedUsernameException ex) {
            model.addAttribute("error", ex.getMessage());
            return "signup";
        }
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/activate/")
    public String activate(Model model) {
        return "scrape";
    }

    @PostMapping("/forgotPassword")
    public String forgotzPassword(Model model) {
        return "scrape";
    }

    @GetMapping("/forgotPassword")
    public String forgotPassword(Model model) {
        return "scrape";
    }


}
