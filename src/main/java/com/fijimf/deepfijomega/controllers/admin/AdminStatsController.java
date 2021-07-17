package com.fijimf.deepfijomega.controllers.admin;

import com.fijimf.deepfijomega.manager.StatisticManager;
import com.fijimf.deepfijomega.repository.SeasonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class AdminStatsController {
    private final StatisticManager mgr;
    private final SeasonRepository seasonRepository;

    @Autowired
    public AdminStatsController(StatisticManager mgr, SeasonRepository seasonRepository) {
        this.mgr = mgr;
        this.seasonRepository = seasonRepository;
    }
    @GetMapping("/admin/models")
    public String index(Model model) {
        model.addAttribute("models", mgr.getModelKeys());
        model.addAttribute("seasons", seasonRepository.findAll());
        return "modelIndex";
    }
    @GetMapping("/admin/models/index")
    public String showJobs(Model model) {
        model.addAttribute("models", mgr.getModelKeys());
        model.addAttribute("seasons", seasonRepository.findAll());
        return "modelIndex";
    }

    @GetMapping("/admin/models/{modelKey}/{season}")
    public String runModel(Model model, @PathVariable String modelKey, @PathVariable String season) {
        if (season.equalsIgnoreCase("all")){
            seasonRepository.findAll().forEach(s-> mgr.runModel(s.getYear(), modelKey));
        } else {
            int year = Integer.parseInt(season);
            mgr.runModel(year, modelKey);
        }

        model.addAttribute("models", mgr.getModelKeys());
        model.addAttribute("seasons", seasonRepository.findAll());
        return "modelIndex";
    }

}
