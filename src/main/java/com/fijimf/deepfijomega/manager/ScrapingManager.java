package com.fijimf.deepfijomega.manager;

import com.fijimf.deepfijomega.entity.schedule.Season;
import com.fijimf.deepfijomega.entity.scraping.ScrapeJob;
import com.fijimf.deepfijomega.entity.scraping.ScrapeRequest;
import com.fijimf.deepfijomega.entity.scraping.SeasonScrapeModel;
import com.fijimf.deepfijomega.repository.ScrapeJobRepository;
import com.fijimf.deepfijomega.repository.ScrapeRequestRepository;
import com.fijimf.deepfijomega.repository.SeasonRepository;
import com.fijimf.deepfijomega.repository.SeasonScrapeModelRepository;
import com.fijimf.deepfijomega.scraping.CasablancaScraper;
import com.fijimf.deepfijomega.scraping.RequestResult;
import com.fijimf.deepfijomega.scraping.ScheduleUpdater;
import com.fijimf.deepfijomega.scraping.UpdateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskDecorator;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class ScrapingManager {
    public static final Logger logger = LoggerFactory.getLogger(ScrapingManager.class);

    private final SeasonRepository seasonRepo;

    private final SeasonScrapeModelRepository modelRepo;

    private final ScrapeJobRepository jobRepo;

    private final ScrapeRequestRepository reqRepo;

    private final CasablancaScraper cbs;

    private final ScheduleUpdater scheduleUpdater;

    private final ThreadPoolTaskExecutor executor;

    public ScrapingManager(SeasonRepository seasonRepo, SeasonScrapeModelRepository modelRepo, ScrapeJobRepository jobRepo, ScrapeRequestRepository reqRepo, CasablancaScraper cbs, ScheduleUpdater scheduleUpdater) {
        this.seasonRepo = seasonRepo;
        this.modelRepo = modelRepo;
        this.jobRepo = jobRepo;
        this.reqRepo = reqRepo;
        this.cbs = cbs;
        this.scheduleUpdater = scheduleUpdater;
        executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(2048);
        executor.initialize();
    }

    public List<SeasonScrapeModel> loadModels() {
        return modelRepo.findAll(Sort.by("year"));
    }

    @Scheduled(cron = "0 0 * * * ?", zone = "America/New_York")
    public void automaticFill() {
        List<SeasonScrapeModel> models = loadModels();
        models.forEach(m -> {
            int year = m.getYear();
            if (seasonRepo.getSeasonGameCount(year) ==0) {
                fillSeason(year, null);
            }
        });
    }
    @Scheduled(cron = "0 0/15 * * 1,2,3,4,11,12 ?", zone = "America/New_York")
    public void automaticUpdate() {
        List<SeasonScrapeModel> models = loadModels();
        if (models.size() > 0) {
            fillSeason(models.get(models.size() - 1).getYear(), LocalDate.now());
        }
    }

    private void updateSeason(Integer year) {

    }

    public long fillSeason(Integer year, LocalDate updateAsOf) {
        Optional<SeasonScrapeModel> model = modelRepo.findFirstByYear(year);
        if (model.isEmpty()) {
            logger.warn("Could not find model for season " + year);
            return 0;
        } else {
            logger.info("Found model " + model.get().getModelName() + " for year " + year);
            return fillSeason(model.get(),updateAsOf);
        }
    }

    private long fillSeason(final SeasonScrapeModel seasonScrapeModel, LocalDate updateAsOf) {
        if (seasonScrapeModel.getModelName().equalsIgnoreCase("Casablanca")) {
            logger.info("Filling season based on Casablanca scraper");
            final ScrapeJob job = jobRepo.save(new ScrapeJob("FILL", seasonScrapeModel.getYear(), seasonScrapeModel.getModelName(), LocalDateTime.now(), null, List.of()));
            Runnable runnable = () -> {
                Season season = findOrCreateSeason(seasonScrapeModel);
                List<LocalDate> dates = updateAsOf == null ? season.getSeasonDates() : updateAsOf.minusDays(7).datesUntil(updateAsOf.plusDays(14)).collect(Collectors.toList());
                dates.forEach(d -> processRequest(job, d));
                job.setCompletedAt(LocalDateTime.now());
                jobRepo.save(job);
            };
            executor.execute(runnable);
            return job.getId();
        } else {
            return -1;
        }
    }

    private void processRequest(ScrapeJob job, LocalDate d) {
        RequestResult requestResult = cbs.scrape(d);
        String modelKey = d.format(DateTimeFormatter.BASIC_ISO_DATE);
        UpdateResult updateResult = scheduleUpdater.updateGamesAndResults(modelKey, requestResult.getUpdateCandidates());
        reqRepo.save(new ScrapeRequest(
                job.getId(),
                modelKey,
                requestResult.getStart(),
                requestResult.getReturnCode(),
                requestResult.getDigest(),
                requestResult.getUpdateCandidates().size(), updateResult.getChanges()
        ));
    }


    private Season findOrCreateSeason(SeasonScrapeModel seasonScrapeModel) {
        return seasonRepo.findFirstByYear(seasonScrapeModel.getYear())
                .orElseGet(() -> seasonRepo.save(new Season(seasonScrapeModel.getYear())));
    }
}

