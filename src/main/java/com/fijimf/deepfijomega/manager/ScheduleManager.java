package com.fijimf.deepfijomega.manager;

import com.fijimf.deepfijomega.entity.schedule.Conference;
import com.fijimf.deepfijomega.entity.schedule.Season;
import com.fijimf.deepfijomega.entity.schedule.Team;
import com.fijimf.deepfijomega.repository.ConferenceRepository;
import com.fijimf.deepfijomega.repository.GameRepository;
import com.fijimf.deepfijomega.repository.SeasonRepository;
import com.fijimf.deepfijomega.repository.TeamRepository;
import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ScheduleManager {
    @Autowired
    TeamRepository teamRepo;

    @Autowired
    ConferenceRepository conferenceRepo;

    @Autowired
    GameRepository gameRepo;

    @Autowired
    SeasonRepository seasonRepo;

    public List<Season> getSeasons() {
        return IteratorUtils.toList(seasonRepo.findAll().iterator());
    }

    public Optional<Season> getCurrentSeason() {
        return getSeasons().stream().max(Comparator.comparingInt(Season::getYear));
    }

    public List<Team> getTeams() {
        List<Team> teams = IteratorUtils.toList(teamRepo.findAll().iterator());
        teams.sort((t1,t2)->t1.getName().compareToIgnoreCase(t2.getName()));
        return teams;
    }

    public List<Team> getConferences() {
        return IteratorUtils.toList(conferenceRepo.findAll().iterator());
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
