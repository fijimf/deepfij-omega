package com.fijimf.deepfijomega.controllers;

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
    @Autowired
    UserRepository userRepository;

    @GetMapping("/admin")
    public String adminMenu(Model model) {
        return "admin/adminMenu";
    }

    @GetMapping("/admin/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return ("admin/users");
    }

    @GetMapping("/admin/force-active/{id}")
    public ModelAndView manageUsers(Model model, @PathVariable("id") Long id) {
        userRepository.findById(id).ifPresent(u -> {
            u.setActivated(true);
            userRepository.save(u);
        });
        return new ModelAndView("redirect:/admin/users");
    }

    @GetMapping("/admin/lock/{id}")
    public ModelAndView lock(Model model, @PathVariable("id") Long id) {
        userRepository.findById(id).ifPresent(u -> {
            u.setLocked(true);
            userRepository.save(u);
        });
        return new ModelAndView("redirect:/admin/users");
    }

    @GetMapping("/admin/unlock/{id}")
    public ModelAndView unlock(Model model, @PathVariable("id") Long id) {
        userRepository.findById(id).ifPresent(u -> {
            u.setLocked(false);
            userRepository.save(u);
        });
        return new ModelAndView("redirect:/admin/users");
    }

    @GetMapping("/admin/delete/{id}")
    public ModelAndView delete(Model model, @PathVariable("id") Long id) {
        userRepository.deleteById(id);
        return new ModelAndView("redirect:/admin/users");
    }

    @GetMapping("/admin/no-expire/{id}")
    public ModelAndView clearExpiry(Model model, @PathVariable("id") Long id) {
        userRepository.findById(id).ifPresent(u -> {
            u.setExpireCredentialsAt(null);
            userRepository.save(u);
        });
        return new ModelAndView("redirect:/admin/users");
    }

    @GetMapping(value = "/admin/set-expire/{id}", params = {"minutes"})
    public ModelAndView setExpiry(Model model, @PathVariable("id") Long id, @Param("minutes") Long minutes) {
        userRepository.findById(id).ifPresent(u -> {
            if (minutes != null && minutes > 0L) {
                u.setExpireCredentialsAt(LocalDateTime.now().plusMinutes(minutes));
                userRepository.save(u);
            }
        });
        return new ModelAndView("redirect:/admin/users");
    }
}
