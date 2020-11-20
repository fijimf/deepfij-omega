package com.fijimf.deepfijomega.controllers;

import com.fijimf.deepfijomega.controllers.forms.ForgotPasswordForm;
import com.fijimf.deepfijomega.manager.ScheduleManager;
import com.fijimf.deepfijomega.scraping.CasablancaScraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@RestController
public class TeamsController {

    @Autowired
    private ScheduleManager scheduleManager;

    @GetMapping("/teams")
    public String forgotPasswordForm(Model model, HttpServletRequest request) throws ServletException {

        return "teams/index";
    }

}
