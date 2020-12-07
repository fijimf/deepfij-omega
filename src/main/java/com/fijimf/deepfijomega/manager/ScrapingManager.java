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
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
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
        executor=new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(2048);
        executor.initialize();
    }

    public List<SeasonScrapeModel> loadModels(){
        return modelRepo.findAll(Sort.by("year"));
    }

    public long fillSeason(Integer year) {
        Optional<SeasonScrapeModel> model = modelRepo.findFirstByYear(year);
        if (model.isEmpty()) {
            logger.warn("Could not find model for season " + year);
            return 0;
        } else {
            logger.info("Found model " + model.get().getModelName() + " for year " + year);
            return fillSeason(model.get());
        }
    }

    private long fillSeason(final SeasonScrapeModel seasonScrapeModel) {
        if (seasonScrapeModel.getModelName().equalsIgnoreCase("Casablanca")) {
            logger.info("Filling season based on Casablanca scraper");
            final ScrapeJob job = jobRepo.save(new ScrapeJob("FILL", seasonScrapeModel.getYear(), seasonScrapeModel.getModelName(), LocalDateTime.now(), null, List.of()));
            Runnable runnable = () -> {
                Season season = findOrCreateSeason(seasonScrapeModel);
                season.getSeasonDates().forEach(d -> processRequest(job, d));
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

