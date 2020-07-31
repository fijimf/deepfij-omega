package com.fijimf.deepfijomega.scraping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public class UpdateCandidate {
    private final LocalDateTime dateTime;
    private final String homeKey;
    private final String awayKey;
    private final Optional<String> location;
    private final Optional<Boolean> isNeutral;
    private final Optional<Integer> homeScore;
    private final Optional<Integer> awayScore;
    private final Optional<Integer> numPeriods;
    private final LocalDate date;

    public UpdateCandidate(LocalDateTime dateTime, String homeKey, String awayKey, Optional<String> location, Optional<Boolean> isNeutral, Optional<Integer> homeScore, Optional<Integer> awayScore, Optional<Integer> numPeriods) {
        this.dateTime = dateTime;
        this.homeKey = homeKey;
        this.awayKey = awayKey;
        this.location = location;
        this.isNeutral = isNeutral;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.numPeriods = numPeriods;
        this.date = dateTime.toLocalDate();
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
        return location;
    }

    public Optional<Boolean> getIsNeutral() {
        return isNeutral;
    }

    public Optional<Integer> getHomeScore() {
        return homeScore;
    }

    public Optional<Integer> getAwayScore() {
        return awayScore;
    }

    public Optional<Integer> getNumPeriods() {
        return numPeriods;
    }

    public LocalDate getDate() {
        return date;
    }

//    def toGame(id: Long, key: GameKey, loadKey: String): Game = Game(id, key.seasonId, date, dateTime, key.homeTeamId, key.awayTeamId, location, isNeutral, loadKey)

//        def toOptionResult(id: Long, gameId: Long): Option[Result] = {
//            (homeScore, awayScore) match {
//                case (Some(h), Some(a)) => Some(Result(0L, 0L, h, a, numPeriods.getOrElse(2)))
//                case _ => None
//            }
//        }
}