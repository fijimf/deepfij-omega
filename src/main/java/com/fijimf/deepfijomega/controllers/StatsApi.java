package com.fijimf.deepfijomega.controllers;

import com.fijimf.deepfijomega.entity.schedule.Team;
import com.fijimf.deepfijomega.entity.stats.Snapshot;
import com.fijimf.deepfijomega.manager.StatisticManager;
import com.fijimf.deepfijomega.repository.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class StatsApi {

    public static final String MEAN = "mean";
    public static final String STDDEV = "stddev";
    public static final String MIN = "min";
    public static final String Q_1 = "q1";
    public static final String MEDIAN = "median";
    public static final String KURTOSIS = "kurtosis";
    public static final String SKEWNESS = "skewness";
    public static final String MAX = "max";
    public static final String Q_3 = "q3";
    @Autowired
    private final ModelRepository modelRepo;

    @Autowired
    private final ModelRunRepository mrRepo;

    @Autowired
    private final StatisticRepository statisticRepository;

    @Autowired
    private final SeriesRepository seriesRepository;

    @Autowired
    private final SeasonRepository seasonRepo;
    @Autowired
    private final TeamRepository teamRepo;
    @Autowired
    private final StatisticManager statMgr;

    private Map<String, Long> teamMap = new HashMap<>();

    public static class DataPoint{
        private final LocalDate date;
        private final Map<String, Double> values;

        public DataPoint(LocalDate date, Map<String, Double> values) {
            this.date = date;
            this.values = values;
        }

        public LocalDate getDate() {
            return date;
        }

        public Map<String, Double> getValues() {
            return values;
        }
    }

    public StatsApi(ModelRepository modelRepo, ModelRunRepository mrRepo, StatisticRepository statisticRepository, SeriesRepository seriesRepository, SeasonRepository seasonRepo, TeamRepository teamRepo, StatisticManager statMgr) {
        this.modelRepo = modelRepo;
        this.mrRepo = mrRepo;
        this.statisticRepository = statisticRepository;
        this.seriesRepository = seriesRepository;
        this.seasonRepo = seasonRepo;
        this.teamRepo = teamRepo;
        this.statMgr = statMgr;
    }

    @PostConstruct
    public void init() {
        teamMap = teamRepo.findAll().stream().collect(Collectors.toMap(Team::getKey, Team::getId));
    }

    @GetMapping("/api/stats/series/{model}/{stat}/{season}")
    public List<DataPoint> getSeries(@PathVariable("model") String model, @PathVariable("stat") String stat, @PathVariable("season") Integer season, @RequestParam("key") List<String> keys) {
        Optional<List<DataPoint>> p =
                statMgr.getSeries(model, stat, season)
                        .map(ser -> ser.getSnapshots().stream().map(snap -> new DataPoint(snap.getDate(), extractFromSnap(keys, snap)))
                                .collect(Collectors.toList()));
        return p.orElse(Collections.emptyList());
    }

    private Map<String, Double> extractFromSnap(List<String> keys, Snapshot snap) {
        Map<String, Double> r = new HashMap<>();
        keys.forEach(k -> {
            if (teamMap.containsKey(k)) {
                Map<Long, Double> tm = snap.getTeamMap();
                Long teamId = teamMap.get(k);
                if (tm.containsKey(teamId)) {
                    Double d = tm.get(teamId);
                    r.put(k, d);
                }
            } else if (k.equalsIgnoreCase(MEAN)) {
                r.put(MEAN, snap.getStatistics().getMean());
            } else if (k.equalsIgnoreCase(STDDEV)) {
                r.put(STDDEV, snap.getStatistics().getStandardDeviation());
            } else if (k.equalsIgnoreCase(MIN)) {
                r.put(MIN, snap.getStatistics().getMin());
            } else if (k.equalsIgnoreCase(Q_1)) {
                r.put(Q_1, snap.getStatistics().getPercentile(25));
            } else if (k.equalsIgnoreCase(MEDIAN)) {
                r.put(MEDIAN, snap.getStatistics().getPercentile(50));
            } else if (k.equalsIgnoreCase(Q_3)) {
                r.put(Q_3, snap.getStatistics().getPercentile(75));
            } else if (k.equalsIgnoreCase(MAX)) {
                r.put(MAX, snap.getStatistics().getMax());
            } else if (k.equalsIgnoreCase(SKEWNESS)) {
                r.put(SKEWNESS, snap.getStatistics().getSkewness());
            } else if (k.equalsIgnoreCase(KURTOSIS)) {
                r.put(KURTOSIS, snap.getStatistics().getKurtosis());
            }
        });
        return r;
    }
}
