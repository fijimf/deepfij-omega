package com.fijimf.deepfijomega.analyticmodel;

import com.fijimf.deepfijomega.entity.schedule.ConferenceMapping;
import com.fijimf.deepfijomega.entity.schedule.Game;
import com.fijimf.deepfijomega.entity.schedule.Season;
import com.fijimf.deepfijomega.entity.stats.Statistic;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WonLost implements AnalyticModel {

    private final Statistic wins = new Statistic("wins", "Wins", 0L,true, 0.0, "%.0f");
    private final Statistic losses = new Statistic("losses", "Losses", 0L, false, 0.0, "%.0f");
    private final Statistic wp = new Statistic("wp", "Win Pct", 0L, true, null, "%.4f");
    private final Statistic wStreak = new Statistic("wstreak", "Win Streak", 0L, true, 0.0, "%.0f");
    private final Statistic lStreak = new Statistic("lstreak", "Loss Streak", 0L, false, 0.0, "%.0f");
    private final List<Statistic> statistics = List.of(wins, losses, wp, wStreak, lStreak);


    @Override
    public String getModelName() {
        return "WonLost";
    }

    @Override
    public String getModelKey() {
        return "won-lost";
    }

    @Override
    public List<Statistic> getModelStatistics() {
        return statistics;
    }

    @Override
    public Map<String, Map<LocalDate, Map<Long, Double>>> runModel(Season season) {
        List<Long> teams = season.getConferenceMapping().stream().map(ConferenceMapping::getTeamId).collect(Collectors.toList());
        Map<LocalDate, Map<Long, Double>> ws = new HashMap<>();
        Map<LocalDate, Map<Long, Double>> ls = new HashMap<>();
        Map<LocalDate, Map<Long, Double>> wps = new HashMap<>();
        Map<LocalDate, Map<Long, Double>> wss = new HashMap<>();
        Map<LocalDate, Map<Long, Double>> lss = new HashMap<>();

        Map<LocalDate, List<Game>> gamesByDate = season.getGames().stream().filter(Game::hasResult).collect(Collectors.groupingBy(Game::getDate));
        LocalDate startDate = gamesByDate.keySet().stream().min(LocalDate::compareTo).orElse(season.getSeasonDates().get(0));
        LocalDate endDate = gamesByDate.keySet().stream().max(LocalDate::compareTo).orElse(startDate);

        Map<Long, Double> oWins = getInitialObservations(teams, wins);
        Map<Long, Double> oLosses = getInitialObservations(teams, losses);
        Map<Long, Double> oWinStreak = getInitialObservations(teams, wStreak);
        Map<Long, Double> oLossStreak = getInitialObservations(teams, lStreak);
        Map<Long, Double> oWp = new HashMap<>();
        startDate.datesUntil(endDate).forEach(d -> {
            List<Game> games = gamesByDate.getOrDefault(d, List.of());
            games.forEach(g -> {
                g.getWinnerId().ifPresent(w -> {
                    if (oWins.containsKey(w)) {
                        oWins.put(w, oWins.get(w) + 1);
                        oWinStreak.put(w, oWinStreak.get(w) + 1);
                        oLossStreak.put(w, 0.0);
                    }
                });
                g.getLoserId().ifPresent(l -> {
                    if (oLosses.containsKey(l)) {
                        oLosses.put(l, oLosses.get(l) + 1);
                        oWinStreak.put(l, 0.0);
                        oLossStreak.put(l, oLossStreak.get(l) + 1);
                    }
                });
            });
            teams.forEach(i -> {
                double wins = oWins.getOrDefault(i, 0.0);
                double losses = oLosses.getOrDefault(i, 0.0);
                if (wins + losses > 0.0) {
                    oWp.put(i, wins / (wins + losses));
                }
            });
            ws.put(d, new HashMap<>(oWins));
            ls.put(d, new HashMap<>(oLosses));
            wss.put(d, new HashMap<>(oWinStreak));
            lss.put(d, new HashMap<>(oLosses));
            wps.put(d, new HashMap<>(oWp));
        });

        return Map.of(
                wins.getKey(), ws,
                losses.getKey(), ls,
                wStreak.getKey(), wss,
                lStreak.getKey(), lss,
                wp.getKey(), wps
        );
    }

    @NotNull
    private Map<Long, Double> getInitialObservations(List<Long> teams, Statistic stat) {
        return teams.stream().collect(Collectors.toMap(Function.identity(), t -> stat.getDefaultValue()));
    }
}
