package com.fijimf.deepfijomega.controllers;

import com.fijimf.deepfijomega.entity.schedule.Conference;
import com.fijimf.deepfijomega.entity.schedule.Team;
import com.fijimf.deepfijomega.manager.ScheduleManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@Controller
public class TeamsController {

    @Autowired
    ScheduleManager manager;

    @GetMapping("/teams")
    public String teams(Model model) {
        model.addAttribute("teams", manager.getTeams());
        model.addAttribute("conferenceToTeam", manager.getCurrentTeamToConferenceMap());
        return "teams/index";
    }
    @GetMapping("/teams/{key}")
    public String team(Model model, @PathVariable(name="key") String key) {
        Team team = manager.getTeam(key);
        System.err.println(team.getColor1());
        System.err.println(team.getFontColor());
        System.err.println(team.getColorCorrectLogoUrl());
        manager.getCurrentSeason().ifPresent(s->{
            Conference conf = manager.getTeamConference(s, team);
            model.addAttribute("conference", conf);
            model.addAttribute("conferenceStandings", manager.getConferenceTeams(s,conf));
            model.addAttribute("overallRecord", manager.getOverallRecord(s,team));
            model.addAttribute("conferenceRecord", manager.getConferenceRecord(s,team));
        });
        model.addAttribute("team", team);
         return "teams/team";
    }

}
