package com.fijimf.deepfijomega.analyticmodel;

import com.fijimf.deepfijomega.entity.schedule.Game;
import com.fijimf.deepfijomega.entity.schedule.Result;
import com.fijimf.deepfijomega.entity.schedule.Season;
import com.fijimf.deepfijomega.entity.schedule.Team;
import com.fijimf.deepfijomega.entity.stats.Statistic;
import com.fijimf.deepfijomega.manager.ScheduleManager;
import org.apache.commons.collections4.map.DefaultedMap;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import smile.data.DataFrame;
import smile.data.formula.Formula;
import smile.data.type.DataTypes;
import smile.data.type.StructField;
import smile.data.type.StructType;
import smile.data.vector.BaseVector;
import smile.regression.LASSO;
import smile.regression.LinearModel;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Regression implements AnalyticModel {

    @Autowired
    private final ScheduleManager scheduleManager;


    private final List<Statistic> stats = List.of(
            new Statistic("margin-coefficient", "Margin", 0L, true, 0.0, "%.4f"),
            new Statistic("won-lost-coefficient", "WonLost", 0L, true, 0.0, "%.4f"),
            new Statistic("capped-margin-coefficient", "Capped Margin", 0L, false, 0.0, "%.4f"),
            new Statistic("margin-mean-residual", "Margin Mean Resid", 0L, false, 0.0, "%.4f"),
            new Statistic("won-lost-mean-residual", "WonLost Mean Resid", 0L, false, 0.0, "%.4f"),
            new Statistic("capped-margin-mean-residual", "Capped Margin Mean Resid", 0L, false, 0.0, "%.4f"),
            new Statistic("margin-stddev-residual", "Margin Std Resid", 0L, false, 0.0, "%.4f"),
            new Statistic("won-lost-stddev-residual", "WonLost Std Resid", 0L, false, 0.0, "%.4f"),
            new Statistic("capped-margin-stddev-residual", "Capped Margin Std Resid", 0L, false, 0.0, "%.4f")
            );

    public Regression(ScheduleManager scheduleManager) {
        this.scheduleManager = scheduleManager;
    }

    @Override
    public String getModelKey() {
        return "lasso-regression";
    }

    @Override
    public String getModelName() {
        return "Lasso Regression";
    }

    @Override
    public List<Statistic> getModelStatistics() {
        return stats;
    }

    @Override
    public Map<String, Map<LocalDate, Map<Long, Double>>> runModel(Season season) {
        List<Team> teams = scheduleManager.getTeams(season);
        Map<String, Long> teamMap = teams.stream().collect(Collectors.toMap(Team::getKey, Team::getId));

        Map<LocalDate, List<Game>> gamesByDate = season.getGames().stream().filter(Game::hasResult).collect(Collectors.groupingBy(Game::getDate));
        LocalDate startDate = gamesByDate.keySet().stream().min(LocalDate::compareTo).orElse(season.getSeasonDates().get(0));
        LocalDate endDate = gamesByDate.keySet().stream().max(LocalDate::compareTo).orElse(startDate);

        Map<String, Map<LocalDate, Map<Long, Double>>> data = new HashMap<>();
        stats.forEach(s -> data.put(s.getKey(), new HashMap<>()));
        List<Map<String, Object>> gameRows = new ArrayList<>();
        List<String> columnNames = new ArrayList<>();
        columnNames.add("margin");
        columnNames.add("won-lost");
        columnNames.add("capped-margin");
        startDate.datesUntil(endDate).forEach(d -> {
            List<Game> games = gamesByDate.getOrDefault(d, List.of());
            List<Map<String, Object>> todayGameRows = games.stream()
                    .filter(Game::hasResult)
                    .map(this::createGameRow)
                    .collect(Collectors.toList());
            gameRows.addAll(todayGameRows);
            updateCappedMargin(gameRows);

            games.stream()
                    .filter(Game::hasResult)
                    .forEach(g -> {
                        if (!columnNames.contains(g.getHomeTeam().getKey())) columnNames.add(g.getHomeTeam().getKey());
                        if (!columnNames.contains(g.getAwayTeam().getKey())) columnNames.add(g.getAwayTeam().getKey());
                    });
            List<StructField> fields = columnNames.stream().map(t -> {
                if (t.equalsIgnoreCase("capped-margin")){
                    return new StructField(t, DataTypes.DoubleType);
                } else {
                    return new StructField(t, DataTypes.IntegerType);
                }
            }).collect(Collectors.toList());
            StructType struct = new StructType(fields);

            DataFrame frame = DataFrame.of(gameRows, struct);
            LinearModel marginFit = LASSO.fit(Formula.lhs("margin"), frame.drop("won-lost","capped-margin"), 10, 0.01, 120);
            LinearModel cappedMarginFit = LASSO.fit(Formula.lhs("capped-margin"), frame.drop("won-lost","margin"), 10, 0.01, 120);
            LinearModel winLoseFit = LASSO.fit(Formula.lhs("won-lost"), frame.drop("margin","capped-margin"), 10, 0.01, 120);


            HashMap<Long, Double> m = extractCoefficients(teamMap, marginFit);
            HashMap<Long, Double> wl = extractCoefficients(teamMap, winLoseFit);
            HashMap<Long, Double> cm = extractCoefficients(teamMap, cappedMarginFit);

            data.get("margin-coefficient").put(d, m);
            data.get("won-lost-coefficient").put(d, wl);
            data.get("capped-margin-coefficient").put(d, cm);

        });
        return data;
    }

    @NotNull
    private HashMap<Long, Double> extractCoefficients(Map<String, Long> teamMap, LinearModel marginFit) {
        HashMap<Long, Double> m = new HashMap<>();
        for (int i = 0; i< marginFit.schema().fields().length; i++){
            String col = marginFit.schema().field(i).name;
            if (teamMap.containsKey(col)){
                m.put(teamMap.get(col), marginFit.coefficients()[i]);
            }
        }
        return m;
    }

    private void updateCappedMargin(List<Map<String, Object>> gameRows) {
        double[] margins = gameRows
                .stream()
                .map(data -> data.getOrDefault("margin", null))
                .filter(Objects::nonNull)
                .filter(x -> x instanceof Integer)
                .mapToDouble(x -> ((Integer) x).doubleValue())
                .toArray();
        DescriptiveStatistics stats = new DescriptiveStatistics(margins);
        gameRows.forEach(data -> {
            if (data.containsKey("margin")) {
                double margin = ((Integer) data.get("margin")).doubleValue();
                double cap = 2 * stats.getStandardDeviation();
                if (margin > cap) {
                    data.put("capped-margin", cap);
                } else if (margin < -cap) {
                    data.put("capped-margin", -cap);
                } else {
                    data.put("capped-margin", margin);
                }
            }
        });
    }

    @NotNull
    private Map<String, Object> createGameRow(Game g) {
        Result result = g.getResult().orElseThrow();
        Map<String, Object> row = new DefaultedMap<>(0);
        row.put(g.getHomeTeam().getKey(), 1);
        row.put(g.getAwayTeam().getKey(), -1);
        row.put("margin", (result.getHomeScore() - result.getAwayScore()));
        row.put("won-lost", (result.getHomeScore() > result.getAwayScore() ? 1 : -1));
        return row;
    }
}
