package com.fijimf.deepfijomega;

import com.fijimf.deepfijomega.entity.schedule.Season;
import com.fijimf.deepfijomega.entity.scraping.SeasonScrapeModel;
import com.fijimf.deepfijomega.repository.ScrapeJobRepository;
import com.fijimf.deepfijomega.repository.ScrapeRequestRepository;
import com.fijimf.deepfijomega.repository.SeasonRepository;
import com.fijimf.deepfijomega.repository.SeasonScrapeModelRepository;
import com.fijimf.deepfijomega.scraping.CasablancaScraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
public class ScrapingController {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    public ScrapingController(SeasonRepository seasonRepo, SeasonScrapeModelRepository modelRepo, ScrapeJobRepository jobRepo, ScrapeRequestRepository reqRepo, CasablancaScraper cbs) {
        this.seasonRepo = seasonRepo;
        this.modelRepo = modelRepo;
        this.jobRepo = jobRepo;
        this.reqRepo = reqRepo;
        this.cbs = cbs;
    }

    private final SeasonRepository seasonRepo;

    private final SeasonScrapeModelRepository modelRepo;

    private final ScrapeJobRepository jobRepo;

    private final ScrapeRequestRepository reqRepo;

    private final CasablancaScraper cbs;

    public static class ScrapeSeasonLine {
        private final Integer year;
        private final Integer numberOfGames;
        private final String modelName;
        private final Integer lastScrapeNumberOfUpdates;
        private final LocalDateTime lastScrape;
        private final boolean updateable    ;


        public ScrapeSeasonLine(Integer year, Integer numberOfGames, String modelName, Integer lastScrapeNumberOfUpdates, LocalDateTime lastScrape, boolean updateable) {
            this.year = year;
            this.numberOfGames = numberOfGames;
            this.modelName = modelName;
            this.lastScrapeNumberOfUpdates = lastScrapeNumberOfUpdates;
            this.lastScrape = lastScrape;
            this.updateable=updateable;
        }

        public Integer getYear() {
            return year;
        }

        public Integer getNumberOfGames() {
            return numberOfGames;
        }

        public String getModelName() {
            return modelName;
        }

        public Integer getLastScrapeNumberOfUpdates() {
            return lastScrapeNumberOfUpdates;
        }

        public LocalDateTime getLastScrape() {
            return lastScrape;
        }

        public boolean isUpdateable() {
            return updateable;
        }
    }

    @GetMapping("/scrape")
    public String scrapeOverview(Model model) {
        Map<Integer, Season> seasonMap = StreamSupport.stream(seasonRepo.findAll().spliterator(), false)
                .collect(Collectors.toMap(Season::getYear, s -> s));

        Map<Integer, String> modelMap = StreamSupport.stream(modelRepo.findAll().spliterator(), false)
                .collect(Collectors.toMap(SeasonScrapeModel::getYear, SeasonScrapeModel::getModelName));
        List<ScrapeSeasonLine> seasons = seasonMap
                .keySet()
                .stream()
                .sorted(Comparator.reverseOrder())
                .map(y -> {
                            int numberOfGames = seasonMap.get(y).getGames().size();
                            String modelName = modelMap.getOrDefault(y, "-");
                            LocalDate today = LocalDate.now();
                            boolean updateable = modelName.equalsIgnoreCase("Casablanca") && seasonMap.get(y).inSeason(today);
                            return new ScrapeSeasonLine(y, numberOfGames, modelName, null, null, updateable);
                        }
                ).collect(Collectors.toList());
        model.addAttribute("seasons", seasons);
        return "scrape";
    }

    @GetMapping("/scrape/jobs")
    public String showJobs(Model model) {
        model.addAttribute(jobRepo.findAll());
        return "scrapeJobs";
    }

    @GetMapping("/scrape/fill/{season}")
    public String fill(Model model, Integer season) {
        model.addAttribute(jobRepo.findAll());
        return "scrapeJobs";
    }

    @GetMapping("/scrape/update/{yyyymmdd}")
    public String update(Model model, Integer season) {
        model.addAttribute(jobRepo.findAll());
        return "scrapeJobs";
    }


}
