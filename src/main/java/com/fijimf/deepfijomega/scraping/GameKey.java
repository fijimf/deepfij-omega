package com.fijimf.deepfijomega.scraping;

import com.fijimf.deepfijomega.entity.schedule.Game;

import java.time.LocalDate;
import java.util.Objects;

public class GameKey {
    public final LocalDate date;
    public final String homeKey;
    public final String awayKey;

    public GameKey(LocalDate date, String homeKey, String awayKey) {
        this.date = date;
        this.homeKey = homeKey;
        this.awayKey = awayKey;
    }

    public static GameKey of(Game g) {
        return new GameKey(g.getDate(), g.getHomeTeam().getKey(), g.getAwayTeam().getKey());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameKey gameKey = (GameKey) o;
        return date.equals(gameKey.date) &&
                homeKey.equals(gameKey.homeKey) &&
                awayKey.equals(gameKey.awayKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, homeKey, awayKey);
    }
}
