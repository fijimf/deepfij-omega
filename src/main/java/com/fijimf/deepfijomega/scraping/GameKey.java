package com.fijimf.deepfijomega.scraping;

import com.fijimf.deepfijomega.entity.schedule.Game;

import java.util.Objects;

public class GameKey {
    public final String key;
    public final String homeKey;
    public final String awayKey;

    public GameKey(String key, String homeKey, String awayKey) {
        this.key = key;
        this.homeKey = homeKey;
        this.awayKey = awayKey;
    }

    public static GameKey of(Game g) {
        return new GameKey(g.getLoadKey(), g.getHomeTeam().getKey(), g.getAwayTeam().getKey());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameKey gameKey = (GameKey) o;
        return key.equals(gameKey.key) &&
                homeKey.equals(gameKey.homeKey) &&
                awayKey.equals(gameKey.awayKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, homeKey, awayKey);
    }

    @Override
    public String toString() {
        return String.format("GameKey[%s %s %s]", key, homeKey, awayKey);
    }
}
