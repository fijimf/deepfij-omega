package com.fijimf.deepfijomega.controllers.admin;

import com.fijimf.deepfijomega.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;

@Controller
public class AdminUserController {

    public static final ModelAndView REDIRECT_ADMIN_USERS = new ModelAndView("redirect:/admin/users");

    final UserRepository userRepository;

    @Autowired
    public AdminUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/admin/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return ("admin/users");
    }

    @GetMapping("/admin/users/force-active/{id}")
    public ModelAndView manageUsers(@PathVariable("id") Long id) {
        userRepository.findById(id).ifPresent(u -> {
            u.setActivated(true);
            userRepository.save(u);
        });
        return REDIRECT_ADMIN_USERS;
    }

    @GetMapping("/admin/users/lock/{id}")
    public ModelAndView lock(@PathVariable("id") Long id) {
        userRepository.findById(id).ifPresent(u -> {
            u.setLocked(true);
            userRepository.save(u);
        });
        return REDIRECT_ADMIN_USERS;
    }

    @GetMapping("/admin/users/unlock/{id}")
    public ModelAndView unlock(@PathVariable("id") Long id) {
        userRepository.findById(id).ifPresent(u -> {
            u.setLocked(false);
            userRepository.save(u);
        });
        return REDIRECT_ADMIN_USERS;
    }

    @GetMapping("/admin/users/delete/{id}")
    public ModelAndView delete(@PathVariable("id") Long id) {
        userRepository.deleteById(id);
        return REDIRECT_ADMIN_USERS;
    }

    @GetMapping("/admin/users/no-expire/{id}")
    public ModelAndView clearExpiry(@PathVariable("id") Long id) {
        userRepository.findById(id).ifPresent(u -> {
            u.setExpireCredentialsAt(null);
            userRepository.save(u);
        });
        return REDIRECT_ADMIN_USERS;
    }

    @GetMapping(value = "/admin/users/set-expire/{id}", params = {"minutes"})
    public ModelAndView setExpiry(@PathVariable("id") Long id, @Param("minutes") Long minutes) {
        userRepository.findById(id).ifPresent(u -> {
            if (minutes != null && minutes > 0L) {
                u.setExpireCredentialsAt(LocalDateTime.now().plusMinutes(minutes));
                userRepository.save(u);
            }
        });
        return REDIRECT_ADMIN_USERS;
    }
}
