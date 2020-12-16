package com.fijimf.deepfijomega.model;

import com.fijimf.deepfijomega.entity.schedule.Game;
import com.fijimf.deepfijomega.entity.schedule.Team;
import org.jetbrains.annotations.NotNull;

public class WonLostRecord implements Comparable<WonLostRecord> {
    private final int wins;
    private final int losses;

    public WonLostRecord() {
        this.wins = 0;
        this.losses = 0;
    }

    public WonLostRecord(int wins, int losses) {
        this.wins = wins;
        this.losses = losses;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    @Override
    public int compareTo(@NotNull WonLostRecord wl) {
        if (wins - losses == wl.wins - wl.losses) {
            return wins - wl.losses;
        } else {
            return (wins - losses) - (wl.wins - wl.losses);
        }
    }

    public static WonLostRecord ofGame(Game g, Team t){
        if (g.isWinner(t)) {
            return new WonLostRecord(1,0);
        } else if (g.isLoser(t)) {
            return new WonLostRecord(0, 1);
        } else {
            return new WonLostRecord();
        }
    }

    public static WonLostRecord combine(WonLostRecord x, WonLostRecord y){
        return new WonLostRecord(x.wins+y.wins, x.losses+y.losses);
    }
}
