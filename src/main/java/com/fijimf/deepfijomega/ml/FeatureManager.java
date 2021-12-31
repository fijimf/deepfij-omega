package com.fijimf.deepfijomega.ml;

import com.fijimf.deepfijomega.entity.schedule.Conference;
import com.fijimf.deepfijomega.entity.schedule.Game;
import com.fijimf.deepfijomega.entity.schedule.Season;
import com.fijimf.deepfijomega.entity.schedule.Team;
import com.fijimf.deepfijomega.ml.features.DayOfWeekExtractor;
import com.fijimf.deepfijomega.ml.features.FeatureExtractor;
import com.fijimf.deepfijomega.ml.features.MonthExtractor;
import com.fijimf.deepfijomega.repository.ConferenceRepository;
import com.fijimf.deepfijomega.repository.SeasonRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.function.Function;

@Component
public class FeatureManager {

    private final SeasonRepository seasonRepository;
    private final ConferenceRepository conferenceRepository;

    @Autowired
    public FeatureManager(SeasonRepository seasonRepository, ConferenceRepository conferenceRepository) {
        this.seasonRepository = seasonRepository;
        this.conferenceRepository = conferenceRepository;
    }

    //Date features
    public FeatureExtractor<String> dayOfWeekExtractor= DayOfWeekExtractor::getFeature;

    public FeatureExtractor<String> monthExtractor=MonthExtractor::getFeature;

    public FeatureExtractor<Integer> seasonExtractor() {
        return g -> seasonRepository
                .findById(g.getSeasonId())
                .map(Season::getYear)
                .orElseThrow(() -> new RuntimeException("Can't find season for game"));
    }

    public FeatureExtractor<Long> getDayOfSeasonExtractor() {
        return g -> seasonRepository
                .findById(g.getSeasonId())
                .map(s -> s.getFirstDate().until(g.getDate(), ChronoUnit.DAYS))
                .orElseThrow(() -> new RuntimeException("Can't find season for game"));
    }

    public FeatureExtractor<String> homeConferenceExtractor = conferenceExtractor(Game::getHomeTeam, Conference::getKey);

    public FeatureExtractor<String> awayConferenceExtractor = conferenceExtractor(Game::getAwayTeam, Conference::getKey);

    public FeatureExtractor<String> homeConferenceStrengthExtractor = conferenceExtractor(Game::getHomeTeam, Conference::getLevel);

    public FeatureExtractor<String> awayConferenceStrengthExtractor = conferenceExtractor(Game::getAwayTeam, Conference::getLevel);

    public FeatureExtractor<Boolean> isConferenceGameExtractor = g -> Objects.equals(homeConferenceExtractor.getFeature(g),awayConferenceExtractor.getFeature(g));

    private FeatureExtractor<String> conferenceExtractor(Function<Game, Team> teamSelector, Function<Conference, String> mapper) {
        return g -> seasonRepository
                .findById(g.getSeasonId())
                .map(s -> s.getTeamConference(teamSelector.apply(g)))
                .flatMap(conferenceRepository::findById)
                .map(mapper)
                .orElseThrow(() -> new RuntimeException("Game " + g.getId()));
    }
    
}
