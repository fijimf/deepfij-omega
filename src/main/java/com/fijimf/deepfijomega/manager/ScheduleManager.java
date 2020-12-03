package com.fijimf.deepfijomega.manager;

import com.fijimf.deepfijomega.entity.schedule.Conference;
import com.fijimf.deepfijomega.entity.schedule.Season;
import com.fijimf.deepfijomega.entity.schedule.Team;
import com.fijimf.deepfijomega.repository.ConferenceRepository;
import com.fijimf.deepfijomega.repository.GameRepository;
import com.fijimf.deepfijomega.repository.SeasonRepository;
import com.fijimf.deepfijomega.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ScheduleManager {
    private final TeamRepository teamRepo;

    private final ConferenceRepository conferenceRepo;

    private final GameRepository gameRepo;

    private final SeasonRepository seasonRepo;

    @Autowired
    public ScheduleManager(TeamRepository teamRepo, ConferenceRepository conferenceRepo, GameRepository gameRepo, SeasonRepository seasonRepo) {
        this.teamRepo = teamRepo;
        this.conferenceRepo = conferenceRepo;
        this.gameRepo = gameRepo;
        this.seasonRepo = seasonRepo;
    }

    public List<Season> getSeasons() {
        return seasonRepo.findAll();
    }

    public Optional<Season> getCurrentSeason() {
        return getSeasons().stream().max(Comparator.comparingInt(Season::getYear));
    }

    public List<Team> getTeams() {
        return teamRepo.findAll(Sort.by("name"));
    }

    public List<Conference> getConferences() {
        return conferenceRepo.findAll(Sort.by("name"));
    }

    public Map<Long, Long> getCurrentTeamToConferenceMap() {
        Map<Long, Long> teamToConference = new HashMap<>();
        getCurrentSeason().ifPresent(s -> {
            s.getConferenceMapping().forEach(cm -> {
                teamToConference.put(cm.getTeamId(), cm.getConferenceId());
            });
        });
        return teamToConference;
    }


}
