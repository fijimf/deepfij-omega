package com.fijimf.deepfijomega.controllers;

import com.fijimf.deepfijomega.controllers.forms.ChangePasswordForm;
import com.fijimf.deepfijomega.controllers.forms.ForgotPasswordForm;
import com.fijimf.deepfijomega.controllers.forms.UserForm;
import com.fijimf.deepfijomega.entity.user.User;
import com.fijimf.deepfijomega.mailer.Mailer;
import com.fijimf.deepfijomega.manager.DuplicatedEmailException;
import com.fijimf.deepfijomega.manager.DuplicatedUsernameException;
import com.fijimf.deepfijomega.manager.UserManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Controller
public class UserController {

    public static final Logger logger = LoggerFactory.getLogger(UserController.class);
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
        //TODO make sure user isn't signed in
        model.addAttribute("user", new User());
        return "user/signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute UserForm user, Model model, HttpServletRequest request) {
        model.addAttribute("email", user.getEmail());
        model.addAttribute("username", user.getUsername());
        try {
            String authCode = userManager.createNewUser(user.getUsername(), user.getPassword(), user.getEmail(), List.of("USER"));
            mailer.sendAuthEmail(user.getUsername(), user.getEmail(), authCode, request.getServerName());
            return "user/signupComplete";
        } catch (IllegalArgumentException ex) {
            logger.warn("Illegal argument creating user", ex);
            model.addAttribute("error", ex.getMessage());
            return "user/signup";
        } catch (DuplicatedEmailException ex) {
            logger.warn("", ex);
            model.addAttribute("error", ex.getMessage());
            return "user/signup";
        } catch (DuplicatedUsernameException ex) {
            model.addAttribute("error", ex.getMessage());
            return "user/signup";
        } catch (MessagingException | IOException e) {
            logger.error("", e);
            return "user/signupComplete"; //TODo Replace with 'Unspecified error.  Try again later'
        }
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "user/login";
    }

    @GetMapping("/activate/{token}")
    public String activate(Model model, @PathVariable("token") String token) {
        userManager.activateUser(token);
        return "user/login";
    }

    @GetMapping("/changePassword")
    public String changePasswordForm(Model model) {
        model.addAttribute("changePassword", new ChangePasswordForm("", ""));
        return "user/changePassword";
    }

    @PostMapping("/changePassword")
    public ModelAndView changePassword(@ModelAttribute ChangePasswordForm cp, Model model) {
        Object p = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (p instanceof User) {
            User u = (User) p;
            String username = u.getUsername();
            if (!username.equals("anonymous")) {
                userManager.changePassword(username, cp.getOldPassword(), cp.getNewPassword()).ifPresent(mailer::sendPasswordChanged);
            }
        }
        return new ModelAndView("redirect:/");
    }

    @GetMapping("/forgotPassword")
    public String forgotPasswordForm(Model model, HttpServletRequest request) throws ServletException {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() != "anonymous") {
            request.logout();
        }
        model.addAttribute("forgotPassword", new ForgotPasswordForm(""));
        return "user/forgotPassword";
    }

    @PostMapping("/forgotPassword")
    public String forgotPassword(@ModelAttribute ForgotPasswordForm forgotPassword, Model model) {
        String email = forgotPassword.getEmail();
        if (StringUtils.isNotBlank(email)) {
            userManager.forgottenPassword(email).ifPresent(password ->
                    userManager.forgottenUser(email).ifPresent(name -> {
                        try {
                            mailer.sendForgotPasswordEmail(email, name, password);
                        } catch (MessagingException | IOException e) {
                            logger.error("Failed to send password reset to {}", email, e);
                        }
                    }));

        }
        ;

        return "user/login";

    }

}
