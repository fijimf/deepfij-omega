package com.fijimf.deepfijomega.analyticmodel;

import com.fijimf.deepfijomega.entity.schedule.ConferenceMapping;
import com.fijimf.deepfijomega.entity.schedule.Game;
import com.fijimf.deepfijomega.entity.schedule.Season;
import com.fijimf.deepfijomega.entity.stats.Statistic;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class Scoring extends AbstractAnalyticModel implements AnalyticModel {

    private final Statistic meanPointsFor = new Statistic("points-for-mean", "Mean Points For", 0L, true, null, "%.4f");
    private final Statistic stdDevPointsFor = new Statistic("points-for-stddev", "Std Dev Points For", 0L, false, null, "%.4f");
    private final Statistic minPointsFor = new Statistic("points-for-min", "Min Points For", 0L, true, null, "%.0f");
    private final Statistic maxPointsFor = new Statistic("points-for-max", "Max Points For", 0L, true, null, "%.0f");
    private final Statistic meanPointsAgainst = new Statistic("points-against-mean", "Mean Points Against", 0L, false, null, "%.4f");
    private final Statistic stdDevPointsAgainst = new Statistic("points-against-stddev", "Std Dev Points Against", 0L, false, null, "%.4f");
    private final Statistic minPointsAgainst = new Statistic("points-against-min", "Min Points Against", 0L, false, null, "%.0f");
    private final Statistic maxPointsAgainst = new Statistic("points-against-max", "Max Points Against", 0L, false, null, "%.0f");

    private final Statistic meanPointsTotal = new Statistic("points-sum-mean", "Mean Points Total", 0L, true, null, "%.4f");
    private final Statistic stdDevPointsTotal = new Statistic("points-sum-stddev", "Std Dev Points Total", 0L, false, null, "%.4f");
    private final Statistic minPointsTotal = new Statistic("points-sum-min", "Min Points Total", 0L, true, null, "%.0f");
    private final Statistic maxPointsTotal = new Statistic("points-sum-max", "Max Points Total", 0L, true, null, "%.0f");

    private final Statistic meanPointsDiff = new Statistic("points-diff-mean", "Mean Points Diff", 0L, false, null, "%.4f");
    private final Statistic stdDevPointsDiff = new Statistic("points-diff-stddev", "Std Dev Points Diff", 0L, false, null, "%.4f");
    private final Statistic minPointsDiff= new Statistic("points-diff-min", "Min Points Diff", 0L, false, null, "%.0f");
    private final Statistic maxPointsDiff = new Statistic("points-diff-max", "Max Points Diff", 0L, false, null, "%.0f");
    private final Map<Statistic, Function<DescriptiveStatistics, Double>> pfextractors = Map.of(
            meanPointsFor, DescriptiveStatistics::getMean,
            stdDevPointsFor, DescriptiveStatistics::getStandardDeviation,
            maxPointsFor, DescriptiveStatistics::getMax,
            minPointsFor, DescriptiveStatistics::getMin
    );
    private final Map<Statistic, Function<DescriptiveStatistics, Double>> paextractors = Map.of(
            meanPointsAgainst, DescriptiveStatistics::getMean,
            stdDevPointsAgainst, DescriptiveStatistics::getStandardDeviation,
            maxPointsAgainst, DescriptiveStatistics::getMax,
            minPointsAgainst, DescriptiveStatistics::getMin
    );
    private final Map<Statistic, Function<DescriptiveStatistics, Double>> diffextractors = Map.of(
            meanPointsDiff, DescriptiveStatistics::getMean,
            stdDevPointsDiff, DescriptiveStatistics::getStandardDeviation,
            maxPointsDiff, DescriptiveStatistics::getMax,
            minPointsDiff, DescriptiveStatistics::getMin
    );
    private final Map<Statistic, Function<DescriptiveStatistics, Double>> sumextractors = Map.of(
            meanPointsTotal, DescriptiveStatistics::getMean,
            stdDevPointsTotal, DescriptiveStatistics::getStandardDeviation,
            maxPointsTotal, DescriptiveStatistics::getMax,
            minPointsTotal, DescriptiveStatistics::getMin
    );

    @Override
    public String getModelName() {
        return "Scoring";
    }

    @Override
    public String getModelKey() {
        return "scoring";
    }

    @Override
    public List<Statistic> getModelStatistics() {
        ArrayList<Statistic> ss = new ArrayList<>(pfextractors.keySet());
        ss.addAll(paextractors.keySet());
        ss.addAll(diffextractors.keySet());
        ss.addAll(sumextractors.keySet());
        return ss;
    }

    @Override
    public Map<String, Map<LocalDate, Map<Long, Double>>> runModel(Season season) {
        List<Long> teams = loadTeamIds(season);
        List<Accumulator> accumulators = List.of(
                new DescriptiveStatsAccumulator(teams, Scoring::getHomeScore, Scoring::getAwayScore, pfextractors),
                new DescriptiveStatsAccumulator(teams, Scoring::getAwayScore, Scoring::getHomeScore, paextractors),
                new DescriptiveStatsAccumulator(teams, Scoring::getScoreDiff, g->-getScoreDiff(g), diffextractors),
                new DescriptiveStatsAccumulator(teams, Scoring::getScoreSum, Scoring::getScoreSum, sumextractors)
        );
        Map<String, Map<LocalDate, Map<Long, Double>>> data = new HashMap<>();

        Map<LocalDate, List<Game>> gamesByDate = season.getGames().stream().filter(Game::hasResult).collect(Collectors.groupingBy(Game::getDate));
        LocalDate startDate = gamesByDate.keySet().stream().min(LocalDate::compareTo).orElse(season.getSeasonDates().get(0));
        LocalDate endDate = gamesByDate.keySet().stream().max(LocalDate::compareTo).orElse(startDate);


        startDate.datesUntil(endDate).forEach(d -> {
            List<Game> games = gamesByDate.getOrDefault(d, List.of());
            games.forEach(g -> accumulators.forEach(a -> a.accumulate(g)));
            accumulators.forEach(a -> {
                Map<String, Map<Long, Double>> dateData = a.extractValues();
                dateData.keySet().forEach(statKey -> {
                    if (!data.containsKey(statKey)) {
                        data.put(statKey, new HashMap<>());
                    }
                    data.get(statKey).put(d, dateData.get(statKey));
                });
            });
        });
        return data;
    }

    @NotNull
    private List<Long> loadTeamIds(Season season) {
        return season.getConferenceMapping().stream().map(ConferenceMapping::getTeamId).collect(Collectors.toList());
    }

    private static Double getHomeScore(Game g) {
        return g.getResult().map(r -> (double) r.getHomeScore()).orElse(null);
    }

    private static Double getAwayScore(Game g) {
        return g.getResult().map(r -> (double) r.getAwayScore()).orElse(null);
    }

    private static Double getScoreSum(Game g) {
        return g.getResult().map(r -> (double) r.getHomeScore() + r.getAwayScore()).orElse(null);
    }

    private static Double getScoreDiff(Game g) {
        return g.getResult().map(r -> (double) r.getHomeScore() - r.getAwayScore()).orElse(null);
    }
}
