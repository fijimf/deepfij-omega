package com.fijimf.deepfijomega.ml;

import com.fijimf.deepfijomega.entity.schedule.Game;
import com.fijimf.deepfijomega.ml.features.FeatureExtractor;
import com.fijimf.deepfijomega.repository.SeasonRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Period;
import java.time.format.DateTimeFormatter;

public class CalendarExtractor {
    @Autowired
    private final  SeasonRepository seasonRepo;
    public CalendarExtractor(SeasonRepository seasonRepo) {
        this.seasonRepo = seasonRepo;
    }

    public FeatureExtractor<String> month = new FeatureExtractor<>() {
        @Override
        public String getFeature(Game g) {
            return g.getDate().format(DateTimeFormatter.ofPattern("MMMM"));
        }
    };

    public FeatureExtractor<Long> seasonMonth= new FeatureExtractor<>() {
        @Override
        public Long getFeature(Game g) {
            return seasonRepo.findById(g.getSeasonId()).map(s -> s.getFirstDate().datesUntil(g.getDate(), Period.ofMonths(1)).count()).orElseThrow();
        }
    };

    public FeatureExtractor<Long> seasonDay = new FeatureExtractor<Long>() {
        @Override
        public Long getFeature(Game g) {
            return seasonRepo.findById(g.getSeasonId()).map(s -> s.getFirstDate().datesUntil(g.getDate(), Period.ofDays(1)).count()).orElseThrow();
        }
    };

    public FeatureExtractor<String> dayOfWeek=new FeatureExtractor<>() {
        @Override
        public String getFeature(Game g) {
            return g.getDate().format(DateTimeFormatter.ofPattern("EEEE"));
        }
    };

}
