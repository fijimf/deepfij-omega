package com.fijimf.deepfijomega.scraping;

import com.fijimf.deepfijomega.entity.scraping.ScrapeJob;
import com.fijimf.deepfijomega.entity.scraping.SeasonScrapeModel;
import com.fijimf.deepfijomega.repository.ScrapeJobRepository;
import com.fijimf.deepfijomega.repository.ScrapeRequestRepository;
import com.fijimf.deepfijomega.repository.SeasonRepository;
import com.fijimf.deepfijomega.repository.SeasonScrapeModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class Scraper {
    private final SeasonRepository seasonRepo;

    private final SeasonScrapeModelRepository modelRepo;

    private final ScrapeJobRepository jobRepo;

    private final ScrapeRequestRepository reqRepo;

    private final CasablancaScraper cbs;

    public Scraper(SeasonRepository seasonRepo, SeasonScrapeModelRepository modelRepo, ScrapeJobRepository jobRepo, ScrapeRequestRepository reqRepo, CasablancaScraper cbs) {
        this.seasonRepo = seasonRepo;
        this.modelRepo = modelRepo;
        this.jobRepo = jobRepo;
        this.reqRepo = reqRepo;
        this.cbs = cbs;
    }

    public void fillSeason(Integer year) {

        List<SeasonScrapeModel> scrapeModels = modelRepo.findByYear(year);
        if (scrapeModels.isEmpty()) {

        } else {
            fillSeason(scrapeModels.get(0));
        }
    }

    private void fillSeason(SeasonScrapeModel seasonScrapeModel) {
        if (seasonScrapeModel.getModelName().equalsIgnoreCase("Casablanca")) {
            ScrapeJob job = jobRepo.save(new ScrapeJob("FILL", seasonScrapeModel.getYear(), seasonScrapeModel.getModelName(), LocalDateTime.now(), null));
            seasonRepo.findFirstByYear(seasonScrapeModel.getYear());
        } else if (seasonScrapeModel.getModelName().equalsIgnoreCase("Ncaa1")) {

        }
    }


}

