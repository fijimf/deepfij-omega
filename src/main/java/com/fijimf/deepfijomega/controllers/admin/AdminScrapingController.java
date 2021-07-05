package com.fijimf.deepfijomega.controllers.admin;

import com.fijimf.deepfijomega.entity.schedule.Season;
import com.fijimf.deepfijomega.entity.scraping.SeasonScrapeModel;
import com.fijimf.deepfijomega.manager.ScrapingManager;
import com.fijimf.deepfijomega.repository.ScrapeJobRepository;
import com.fijimf.deepfijomega.repository.SeasonRepository;
import com.fijimf.deepfijomega.repository.SeasonScrapeModelRepository;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class AdminScrapingController {

    public static final Logger logger = LoggerFactory.getLogger(AdminScrapingController.class);

    public static class ScrapeSeasonLine {
        private final int year;
        private final int numberOfGames;
        private final String modelName;
        private final int lastScrapeNumberOfUpdates;
        private final LocalDateTime lastScrape;
        private final boolean updatable;


        public ScrapeSeasonLine(int year, int numberOfGames, String modelName, int lastScrapeNumberOfUpdates, LocalDateTime lastScrape, boolean updatable) {
            this.year = year;
            this.numberOfGames = numberOfGames;
            this.modelName = modelName;
            this.lastScrapeNumberOfUpdates = lastScrapeNumberOfUpdates;
            this.lastScrape = lastScrape;
            this.updatable = updatable;
        }

        public int getYear() {
            return year;
        }

        public int getNumberOfGames() {
            return numberOfGames;
        }

        public String getModelName() {
            return modelName;
        }

        public int getLastScrapeNumberOfUpdates() {
            return lastScrapeNumberOfUpdates;
        }

        public LocalDateTime getLastScrape() {
            return lastScrape;
        }

        public boolean isUpdatable() {
            return updatable;
        }
    }

    @Autowired
    public AdminScrapingController(
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
    public ModelAndView fill(@PathVariable("season") Integer season) {
        logger.info("Fill request for season {}", season);
        long id = scrapingManager.fillSeason(season,null);
        return new ModelAndView("redirect:/admin/scrape/job/"+id);
    }

    @GetMapping("/admin/scrape/update/{yyyymmdd}")
    public ModelAndView update(Model model, @PathVariable("yyyymmdd") String yyyymmdd) {
        LocalDate asOf = LocalDate.parse(yyyymmdd, DateTimeFormatter.ofPattern("yyyyMMdd"));
        return seasonRepo.findAll().stream().filter(s -> s.inSeason(asOf)).findFirst().map(
                season -> {
                    logger.info("Update request for season {} as of {}", season.getYear(), yyyymmdd);
                    long id = scrapingManager.fillSeason(season.getYear(), null);
                    return new ModelAndView("redirect:/admin/scrape/job/" + id);
                }).orElse(new ModelAndView("redirect:/admin/scrape"));
        
    }

    @GetMapping("/admin/scrape/job/{id}")
    public String showJob(Model model, @PathVariable("id") long id) {
        jobRepo.findById(id).ifPresent(scrapeJob -> model.addAttribute("job", scrapeJob));
        return "scrapeJob";
    }
}
