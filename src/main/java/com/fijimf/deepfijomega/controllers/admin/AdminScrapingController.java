package com.fijimf.deepfijomega.controllers.admin;

import com.fijimf.deepfijomega.entity.schedule.Game;
import com.fijimf.deepfijomega.entity.schedule.Season;
import com.fijimf.deepfijomega.entity.scraping.ScrapeJob;
import com.fijimf.deepfijomega.entity.scraping.SeasonScrapeModel;
import com.fijimf.deepfijomega.manager.ScrapingManager;
import com.fijimf.deepfijomega.repository.ScrapeJobRepository;
import com.fijimf.deepfijomega.repository.ScrapeRequestRepository;
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
public class AdminScrapingController {

    public static final Logger logger = LoggerFactory.getLogger(AdminScrapingController.class);

    public static class ScrapeSeasonLine {
        private final int year;
        private final int numberOfGames;
        private final int numberOfResults;
        private final String modelName;
        private final int lastScrapeNumberOfUpdates;
        private final LocalDateTime lastRun;
        private final LocalDateTime latestUpdate;
        private final boolean updatable;

        public ScrapeSeasonLine(int year, int numberOfGames, int numberOfResults, String modelName, int lastScrapeNumberOfUpdates, LocalDateTime lastRun, LocalDateTime latestUpdate, boolean updatable) {
            this.year = year;
            this.numberOfGames = numberOfGames;
            this.numberOfResults = numberOfResults;
            this.modelName = modelName;
            this.lastScrapeNumberOfUpdates = lastScrapeNumberOfUpdates;
            this.lastRun = lastRun;
            this.latestUpdate = latestUpdate;
            this.updatable = updatable;
        }

        public int getYear() {
            return year;
        }

        public int getNumberOfGames() {
            return numberOfGames;
        }

        public int getNumberOfResults() {
            return numberOfResults;
        }

        public String getModelName() {
            return modelName;
        }

        public int getLastScrapeNumberOfUpdates() {
            return lastScrapeNumberOfUpdates;
        }

        public LocalDateTime getLastRun() {
            return lastRun;
        }

        public LocalDateTime getLatestUpdate() {
            return latestUpdate;
        }

        public boolean isUpdatable() {
            return updatable;
        }
    }


    private final SeasonRepository seasonRepo;

    private final SeasonScrapeModelRepository modelRepo;

    private final ScrapeJobRepository jobRepo;

    private final ScrapeRequestRepository reqRepo;

    private final ScrapingManager scrapingManager;

    @Autowired
    public AdminScrapingController(
            SeasonRepository seasonRepo,
            SeasonScrapeModelRepository modelRepo,
            ScrapeJobRepository jobRepo,
            ScrapeRequestRepository reqRepo,
            ScrapingManager scrapingManager) {
        this.seasonRepo = seasonRepo;
        this.modelRepo = modelRepo;
        this.jobRepo = jobRepo;
        this.reqRepo = reqRepo;
        this.scrapingManager = scrapingManager;
    }

    @GetMapping("/admin/scrape")
    public String scrapeOverview(Model model) {
        List<Season> seasons = seasonRepo.findAll();
        Map<Integer, SeasonScrapeModel> modelMap = modelRepo.findAll().stream()
                .collect(Collectors.toMap(SeasonScrapeModel::getYear, Function.identity()));
        LocalDate today = LocalDate.now();
        List<ScrapeSeasonLine> lines = seasons
                .stream()
                .map(s -> {
                    int year = s.getYear();
                    SeasonScrapeModel scraper = modelMap.get(year);
                    List<ScrapeJob> jobs = jobRepo.findAllBySeasonAndModelName(year, scraper.getModelName());
                    int numberOfRuns=jobs.size();
                    Optional<ScrapeJob> lastJob = jobs.stream().max(Comparator.comparing(ScrapeJob::getStartedAt));
                    Optional<Integer> changes = lastJob.map(ScrapeJob::getChangesAccepted);
                    Optional<LocalDateTime> lastRun = lastJob.map(ScrapeJob::getStartedAt);
                    int numberOfGames = s.getGames().size();
                    int numberOfResults = (int) s.getGames().stream().filter(Game::hasResult).count();
                            String modelName = scraper.getModelName();
                            boolean updateable = modelName.equalsIgnoreCase("Casablanca") && s.inSeason(today);
                            return new ScrapeSeasonLine(year, numberOfGames, numberOfResults, modelName, changes.orElse(0), lastRun.orElse(null), s.getLastUpdatedAt().orElse(null),  updateable);
                        }
                ).collect(Collectors.toList());
        model.addAttribute("today", today.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        model.addAttribute("seasons", lines);
        return "scrape";
    }

    @GetMapping("/admin/scrape/jobs")
    public String showJobs(Model model, @RequestParam(name = "season", required = false) Integer season) {
        if (season == null) {
            model.addAttribute("jobs", jobRepo.findAll());
        } else {
            model.addAttribute("jobs", jobRepo.findAllBySeason(season));
        }
        return "scrapeJobs";
    }

    @GetMapping("/admin/scrape/fill/{season}")
    public ModelAndView fill(@PathVariable("season") Integer season) {
        logger.info("Fill request for season {}", season);
        long id = scrapingManager.fillSeason(season, null);
        return new ModelAndView("redirect:/admin/scrape/job/" + id);
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

    @GetMapping("/admin/scrape/req/{key}")
    public String showRequestsByKey(Model model, @PathVariable("key") String key) {
       model.addAttribute("requests", reqRepo.findScrapeRequestByModelKey(key));
        return "scrapeRequests";
    }
}
