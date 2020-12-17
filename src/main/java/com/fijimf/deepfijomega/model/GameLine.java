package com.fijimf.deepfijomega.model;

import com.fijimf.deepfijomega.entity.schedule.Game;
import com.fijimf.deepfijomega.entity.schedule.Season;
import com.fijimf.deepfijomega.entity.schedule.Team;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

public class GameLine implements Comparable<GameLine>{
    private final LocalDateTime time;
    private final Team opponent;
    private final String atOrVs;
    private final String scores;
    private final String ot;
    private final String wl;
    private final boolean conferenceGame;
    private final boolean win;
    private final boolean loss;

    public GameLine(LocalDateTime time, Team opponent, String atOrVs, String scores, String ot, String wl, boolean conferenceGame, boolean win, boolean loss) {
        this.time = time;
        this.opponent = opponent;
        this.atOrVs = atOrVs;
        this.scores = scores;
        this.ot = ot;
        this.wl = wl;
        this.conferenceGame = conferenceGame;
        this.win = win;
        this.loss = loss;
    }

    public static GameLine from(Season s, Game g, Team t) {
        if (!g.hasTeam(t)) throw new RuntimeException();
        LocalDateTime time = g.getTime();
        Team opponent = g.getOpponent(t);
        String atOrVs = atOrVs(g, t);
        String scores = g.getResult().map(r -> r.getHomeScore() + "-" + r.getAwayScore()).orElse("");
        String ot = otString(g);
        String wl = wlString(g, t);
        boolean conferenceGame = s.getTeamConference(g.getHomeTeam()).equals(s.getTeamConference(g.getAwayTeam()));
        boolean isWinner = g.isWinner(t);
        boolean isLoser = g.isLoser(t);
        return new GameLine(time, opponent, atOrVs, scores, ot, wl, conferenceGame, isWinner, isLoser);
    }

    private static String wlString(Game g, Team t) {
        if (g.isWinner(t)) {
            return "W";
        } else if (g.isLoser(t)) {
            return "L";
        } else{
            return "";
        }
    }

    private static String otString(Game g) {
        return g.getResult().map(r -> {
            if (r.getNumPeriods() <= 2) {
                return "";
            } else if (r.getNumPeriods() == 3) {
                return "OT";
            } else {
                return (r.getNumPeriods() - 2) + "OT";
            }
        }).orElse("");
    }

    private static String atOrVs(Game g, Team t) {
        if (g.getHomeTeam().getId() == t.getId()) {
            return "@";
        } else {
            return "vs.";
        }
    }

    public LocalDateTime getTime() {
        return time;
    }

    public Team getOpponent() {
        return opponent;
    }

    public String getAtOrVs() {
        return atOrVs;
    }

    public String getScores() {
        return scores;
    }

    public String getOt() {
        return ot;
    }

    public String getWl() {
        return wl;
    }

    public boolean isConferenceGame() {
        return conferenceGame;
    }

    public boolean isWin() {
        return win;
    }

    public boolean isLoss() {
        return loss;
    }

    @Override
    public int compareTo(@NotNull GameLine o) {
        return time.compareTo(o.getTime());
    }
}
