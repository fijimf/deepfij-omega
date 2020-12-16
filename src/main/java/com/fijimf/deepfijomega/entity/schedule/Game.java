package com.fijimf.deepfijomega.entity.schedule;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/*
CREATE TABLE game
(
    id              BIGSERIAL    PRIMARY KEY,
    season_id       BIGINT       NOT NULL REFERENCES season(id),
    date            DATE         NOT NULL,
    time            TIMESTAMP    NOT NULL,
    home_team_id    BIGINT       NOT NULL REFERENCES team(id),
    away_team_id    BIGINT       NOT NULL REFERENCES team(id),
    location        VARCHAR(128) NULL,
    is_neutral      BOOLEAN      NULL,
    load_key        VARCHAR(32)  NOT NULL
);
 */
@Entity
@Table(name = "game")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;//             BIGSERIAL    PRIMARY KEY,
    @Column(name = "season_id")
    private long seasonId;//            season_id       BIGINT       NOT NULL REFERENCES season(id),
    private LocalDate date;//          DATE         NOT NULL,
    private LocalDateTime time;//          TIMESTAMP    NOT NULL,
    @OneToOne(fetch = FetchType.EAGER, optional = false, orphanRemoval = false)
    @JoinColumn(name = "home_team_id", referencedColumnName = "id")
    private Team homeTeam;

    @OneToOne(fetch = FetchType.EAGER, optional = false, orphanRemoval = false)
    @JoinColumn(name = "away_team_id", referencedColumnName = "id")
    private Team awayTeam;
    private String location;//      VARCHAR(128) NULL,
    @Column(name = "is_neutral")
    private boolean isNeutral;//     BOOLEAN      NULL,
    private String loadKey;//     VARCHAR(32)  NOT NULL

    @OneToOne(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.EAGER, optional = true)
    private Result result;


    protected Game() {
    }

    public Game(long seasonId, LocalDate date, LocalDateTime time, Team homeTeam, Team awayTeam, String location, boolean isNeutral, String loadKey, Result result) {
        this.id = 0L;
        this.seasonId = seasonId;
        this.date = date;
        this.time = time;

        this.homeTeam = homeTeam;

        this.awayTeam = awayTeam;
        this.location = location;

        this.isNeutral = isNeutral;
        this.loadKey = loadKey;

        this.result = result;
        if (result != null) result.setGame(this);
    }

    public long getId() {
        return id;
    }

    public long getSeasonId() {
        return seasonId;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public String getLocation() {
        return location;
    }

    public boolean isNeutral() {
        return isNeutral;
    }

    public String getLoadKey() {
        return loadKey;
    }

    public Optional<Result> getResult() {
        return Optional.ofNullable(result);
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setNeutral(boolean neutral) {
        isNeutral = neutral;
    }

    public void setResult(Result result) {
        if (result != null) result.setGame(this);
        this.result = result;
    }

    public boolean updatedNeeded(Game g) {
        return isNeutral != g.isNeutral ||
                !date.equals(g.date) ||
                !time.equals(g.time) ||
                !homeTeam.equals(g.homeTeam) ||
                !awayTeam.equals(g.awayTeam) ||
                !Objects.equals(location, g.location) ||
                !loadKey.equals(g.loadKey) ||
                resultUpdateNeeded(g);
    }

    public boolean resultUpdateNeeded(Game g) {
        if (result == null) {
            return g.result != null;
        } else {
            if (g.result == null) {
                return true;
            } else {
                return result.updateNeeded(g.result);
            }
        }
    }

    public boolean hasTeam(Team team) {
        return isHomeTeam(team) || isAwayTeam(team);
    }

    private boolean isAwayTeam(Team team) {
        return awayTeam.getId() == team.getId();
    }

    private boolean isHomeTeam(Team team) {
        return homeTeam.getId() == team.getId();
    }

    public boolean isWinner(Team t) {
        return result != null &&
                ((isHomeTeam(t) && result.isHomeWinner()) || (isAwayTeam(t) && result.isAwayWinner()));
    }
    public boolean isLoser(Team t) {
        return result != null &&
                ((isHomeTeam(t) && result.isHomeLoser()) || (isAwayTeam(t) && result.isAwayLoser()));
    }
}
