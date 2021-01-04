package com.fijimf.deepfijomega.controllers;

import com.fijimf.deepfijomega.entity.schedule.Conference;
import com.fijimf.deepfijomega.entity.schedule.Team;
import com.fijimf.deepfijomega.manager.ScheduleManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

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
    public String team(Model model, @PathVariable(name="key") String key, @RequestParam() Optional<Integer> year) {
        Team team = manager.getTeam(key);
        List<Integer> years = manager.getSeasonList();
        Optional<Integer> max = years.stream().max(Comparator.comparingInt(v->v));

        year.or(()->max).flatMap(y->manager.getSeasonByYear(y)).ifPresent(s->{
            Conference conf = manager.getTeamConference(s, team);
            Integer currentSeasonYear = max.orElse(0);
            Integer seasonYear = s.getYear();
            model.addAttribute("allYears", years);
            model.addAttribute("year", seasonYear);
            model.addAttribute("currentSeason", currentSeasonYear);
            model.addAttribute("yearQueryString", seasonYear.equals(currentSeasonYear) ?"":("?year="+ seasonYear));
            model.addAttribute("conference", conf);
            model.addAttribute("conferenceStandings", manager.getConferenceTeams(s,conf));
            model.addAttribute("overallRecord", manager.getOverallRecord(s,team));
            model.addAttribute("conferenceRecord", manager.getConferenceRecord(s,team));
            model.addAttribute("results",manager.getGames(s,team) );
        });
        model.addAttribute("team", team);
         return "teams/team";
    }

}
