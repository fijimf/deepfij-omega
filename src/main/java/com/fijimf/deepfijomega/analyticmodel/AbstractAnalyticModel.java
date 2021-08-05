package com.fijimf.deepfijomega.analyticmodel;

import com.fijimf.deepfijomega.entity.schedule.ConferenceMapping;
import com.fijimf.deepfijomega.entity.schedule.Game;
import com.fijimf.deepfijomega.entity.schedule.Season;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public abstract class AbstractAnalyticModel {

    protected Map<LocalDate, List<Game>> getGamesByDate(Season season) {
        return season.getGames().stream().filter(Game::hasResult).collect(Collectors.groupingBy(Game::getDate));
    }

    protected List<Long> getTeams(Season season) {
        return season.getConferenceMapping().stream().map(ConferenceMapping::getTeamId).collect(Collectors.toList());
    }

    protected LocalDate getStartDate(Map<LocalDate, List<Game>> gamesByDate, Season season) {
        return gamesByDate.keySet().stream().min(LocalDate::compareTo).orElse(season.getSeasonDates().get(0));
    }

    protected LocalDate getEndDate(Map<LocalDate, List<Game>> gamesByDate, Season season, LocalDate startDate) {
        return gamesByDate.keySet().stream().max(LocalDate::compareTo).orElse(startDate);
    }

    public void iterateOverSeason(Season season, BiFunction<LocalDate, List<Game>, Void> dayFunction){
        Map<LocalDate, List<Game>> gamesByDate = getGamesByDate(season);
        LocalDate startDate = getStartDate(gamesByDate, season);
        LocalDate endDate = getEndDate(gamesByDate, season, startDate);
        startDate.datesUntil(endDate).forEach(d->{
            List<Game> games = gamesByDate.getOrDefault(d, Collections.emptyList());
            dayFunction.apply(d,games);
        });
    }
}
