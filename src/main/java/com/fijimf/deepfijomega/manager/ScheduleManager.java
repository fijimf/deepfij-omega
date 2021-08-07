package com.fijimf.deepfijomega.manager;

import com.fijimf.deepfijomega.entity.schedule.*;
import com.fijimf.deepfijomega.model.GameLine;
import com.fijimf.deepfijomega.model.WonLostRecord;
import com.fijimf.deepfijomega.repository.AliasRepository;
import com.fijimf.deepfijomega.repository.ConferenceRepository;
import com.fijimf.deepfijomega.repository.SeasonRepository;
import com.fijimf.deepfijomega.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleManager {
    private final TeamRepository teamRepo;
    private final AliasRepository aliasRepo;
    private final ConferenceRepository conferenceRepo;
    private final SeasonRepository seasonRepo;

    @Autowired
    public ScheduleManager(TeamRepository teamRepo, AliasRepository aliasRepo, ConferenceRepository conferenceRepo, SeasonRepository seasonRepo) {
        this.teamRepo = teamRepo;
        this.aliasRepo = aliasRepo;
        this.conferenceRepo = conferenceRepo;
        this.seasonRepo = seasonRepo;
    }

    @Cacheable("seasons")
    public List<Season> getSeasons() {
        return seasonRepo.findAll();
    }

    public Optional<Season> getCurrentSeason() {
        return getSeasons().stream().max(Comparator.comparingInt(Season::getYear));
    }

    public List<Team> getTeams() {
        return teamRepo.findAll(Sort.by("name"));
    }

    public List<Team> getTeams(Season s){
        Set<Long> ts = s.getConferenceMapping().stream().map(ConferenceMapping::getTeamId).collect(Collectors.toSet());
        return getTeams().stream().filter(t->ts.contains(t.getId())).collect(Collectors.toList());
    }
    public List<Conference> getConferences() {
        return conferenceRepo.findAll(Sort.by("name"));
    }

    public Map<Long, Long> getCurrentTeamToConferenceMap() {
        Optional<Map<Long, Long>> map = getCurrentSeason().map(
                s -> s.getConferenceMapping()
                        .stream()
                        .collect(Collectors.toMap(
                                ConferenceMapping::getTeamId,
                                ConferenceMapping::getConferenceId
                        ))

        );
        return map.orElseThrow(() -> new RuntimeException("Cannot get team conference map; no current season"));
    }


    public Team getTeam(String key) {
        return teamRepo.findFirstByKey(key).orElseThrow();
    }

    public Conference getTeamConference(Season s, Team t) {
        return conferenceRepo.findById(s.getTeamConference(t)).orElseThrow(); //TODO
    }

    public List<Team> getConferenceTeams(Season s, Conference c) {
        return s.getConferenceTeams(c.getId())
                .stream()
                .flatMap(id -> teamRepo.findById(id).stream())
                .collect(Collectors.toList());
    }

    public WonLostRecord getOverallRecord(Season s, Team t) {
        return s.getGames()
                .stream()
                .map(g -> WonLostRecord.ofGame(g, t))
                .reduce(WonLostRecord::combine)
                .orElse(new WonLostRecord(0, 0));
    }

    public WonLostRecord getConferenceRecord(Season s, Team t) {
        return s.getGames()
                .stream()
                .filter(g -> g.hasTeam(t))
                .filter(g -> s.getTeamConference(g.getHomeTeam()).equals(s.getTeamConference(g.getAwayTeam())))
                .map(g -> WonLostRecord.ofGame(g, t))
                .reduce(WonLostRecord::combine)
                .orElse(new WonLostRecord(0, 0));
    }

    public List<GameLine> getGames(Season s, Team t) {

        LocalDate lastResult = s.getGames()
                .stream()
                .filter(g11 -> g11.hasTeam(t))
                .filter(g -> g.getResult().isPresent())
                .map(Game::getDate)
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now().minusDays(1));

        return s.getGames()
                .stream()
                .filter(g1 -> g1.hasTeam(t))
                .filter(g -> g.getResult().isPresent() || g.getDate().isAfter(lastResult))
                .map(g -> GameLine.from(s, g, t))
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Integer> getSeasonList() {
        return seasonRepo.getSeasonYears();
    }

    public Optional<Season> getSeasonByYear(Integer year) {
        return seasonRepo.findFirstByYear(year);
    }


    @Cacheable("teams")
    public Optional<Team> findTeam(String key) {
        return teamRepo
                .findFirstByKey(key)
                .or(() -> aliasRepo
                        .findFirstByValue(key)
                        .map(Alias::getTeam)
                );
    }
}
