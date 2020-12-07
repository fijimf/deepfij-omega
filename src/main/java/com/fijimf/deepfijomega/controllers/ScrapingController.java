package com.fijimf.deepfijomega.controllers;

import com.fijimf.deepfijomega.entity.schedule.Season;
import com.fijimf.deepfijomega.entity.scraping.SeasonScrapeModel;
import com.fijimf.deepfijomega.repository.ScrapeJobRepository;
import com.fijimf.deepfijomega.repository.SeasonRepository;
import com.fijimf.deepfijomega.repository.SeasonScrapeModelRepository;
import com.fijimf.deepfijomega.manager.ScrapingManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ScrapingController {

    public static final Logger logger = LoggerFactory.getLogger(ScrapingController.class);

    @Autowired
    public ScrapingController(
            SeasonRepository seasonRepo,
            SeasonScrapeModelRepository modelRepo,
            ScrapeJobRepository jobRepo,
            ScrapingManager scrapingManager) {
        this.seasonRepo = seasonRepo;
        this.modelRepo = modelRepo;
        this.jobRepo = jobRepo;
        this.scrapingManager = scrapingManager;
    }

    private final SeasonRepository seasonRepo;

    private final SeasonScrapeModelRepository modelRepo;

    private final ScrapeJobRepository jobRepo;

    private final ScrapingManager scrapingManager;

    @GetMapping("/admin/scrape")
    public String scrapeOverview(Model model) {
        List<Season> seasons = seasonRepo.findAll();
        Map<Integer, String> modelMap = modelRepo.findAll().stream()
                .collect(Collectors.toMap(SeasonScrapeModel::getYear, SeasonScrapeModel::getModelName));
        LocalDate today = LocalDate.now();
        List<ScrapeSeasonLine> lines = seasons
                .stream()
                .map(s -> {
                            int numberOfGames = s.getGames().size();
                            int year = s.getYear();
                            String modelName = modelMap.getOrDefault(year, "-");
                            boolean updateable = modelName.equalsIgnoreCase("Casablanca") && s.inSeason(today);
                            return new ScrapeSeasonLine(year, numberOfGames, modelName, 0, null, updateable);
                        }
                ).collect(Collectors.toList());
        model.addAttribute("today", today.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        model.addAttribute("seasons", lines);
        return "scrape";
    }

    @GetMapping("/admin/scrape/jobs")
    public String showJobs(Model model, @RequestParam(name="season", required = false) Integer season) {
        if (season==null) {
            model.addAttribute("jobs", jobRepo.findAll());
        } else {
            model.addAttribute("jobs", jobRepo.findAllBySeason(season));
        }
        return "scrapeJobs";
    }

    @GetMapping("/admin/scrape/fill/{season}")
    public ModelAndView fill(Model model, @PathVariable("season") Integer season) {
        logger.info("Fill request for season {}", season);
        long id = scrapingManager.fillSeason(season);
        return new ModelAndView("redirect:/admin/scrape/job/"+id);
    }

    @GetMapping("/admin/scrape/update/{yyyymmdd}")
    public String update(Model model, Integer season) {
        model.addAttribute(jobRepo.findAll());
        return "scrapeJobs";
    }

    @GetMapping("/admin/scrape/job/{id}")
    public String showJob(Model model, @PathVariable("id") long id) {
        jobRepo.findById(id).ifPresent(scrapeJob -> model.addAttribute("job", scrapeJob));
        return "scrapeJob";
    }


}
