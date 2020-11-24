package com.fijimf.deepfijomega.controllers;

import com.fijimf.deepfijomega.manager.ScheduleManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@Controller
public class TeamsController {

    @Autowired
    ScheduleManager manager;

    @GetMapping("/teams")
    public String forgotPasswordForm(Model model) {
        model.addAttribute("teams", manager.getTeams());
        model.addAttribute("conferenceToTeam", manager.getCurrentTeamToConferenceMap());
        return "teams/index";
    }

}
