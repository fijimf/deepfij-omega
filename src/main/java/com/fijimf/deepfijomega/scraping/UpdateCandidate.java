package com.fijimf.deepfijomega.scraping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class UpdateCandidate {
    private final LocalDateTime dateTime;
    private final String homeKey;
    private final String awayKey;
    private final String location;
    private final Boolean isNeutral;
    private final Integer homeScore;
    private final Integer awayScore;
    private final Integer numPeriods;
    private final LocalDate date;
    private final String loadKey;

    public UpdateCandidate(LocalDateTime dateTime,
                           String homeKey,
                           String awayKey,
                           String location,
                           Boolean isNeutral,
                           Integer homeScore,
                           Integer awayScore,
                           Integer numPeriods,
                           String loadKey) {
        this.dateTime = dateTime;
        this.homeKey = homeKey;
        this.awayKey = awayKey;
        this.location = location;
        this.isNeutral = isNeutral;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.numPeriods = numPeriods;
        this.date = dateTime.toLocalDate();
        this.loadKey = loadKey;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getHomeKey() {
        return homeKey;
    }

    public String getAwayKey() {
        return awayKey;
    }

    public Optional<String> getLocation() {
        return Optional.ofNullable(location);
    }

    public Optional<Boolean> getIsNeutral() {
        return Optional.ofNullable(isNeutral);
    }

    public Optional<Integer> getHomeScore() {
        return Optional.ofNullable(homeScore);
    }

    public Optional<Integer> getAwayScore() {
        return Optional.ofNullable(awayScore);
    }

    public Optional<Integer> getNumPeriods() {
        return Optional.ofNullable(numPeriods);
    }

    public LocalDate getDate() {
        return date;
    }

    public String getLoadKey() {
        return loadKey;
    }

    @Override
    public String toString() {
        return "UpdateCandidate{ " +
                ", homeKey='" + homeKey + '\'' +
                ", awayKey='" + awayKey + '\'' +
                ", date=" + date.format(DateTimeFormatter.ISO_DATE) +
                " }";
    }
}