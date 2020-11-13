package com.fijimf.deepfijomega;

import com.fijimf.deepfijomega.entity.user.User;
import com.fijimf.deepfijomega.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;

@Controller
public class AdminController {
    @Autowired
    UserRepository userRepository;

    @GetMapping("/admin")
    public String signupForm(Model model) {
        return "admin/adminMenu";
    }

    @GetMapping("/admin/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return("admin/users");
    }
}
