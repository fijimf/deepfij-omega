package com.fijimf.deepfijomega.entity.schedule;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;//             BIGSERIAL    PRIMARY KEY,
    @Column(name="season_id")
    private long seasonId;//            season_id       BIGINT       NOT NULL REFERENCES season(id),
    private LocalDate date;//          DATE         NOT NULL,
    private LocalDateTime time;//          TIMESTAMP    NOT NULL,
    @Column(name="home_team_id")
    private long homeTeamId;//   BIGINT       NOT NULL REFERENCES team(id),
    @Column(name="away_team_id")
    private long awayTeamId;//   BIGINT       NOT NULL REFERENCES team(id),
    private String location;//      VARCHAR(128) NULL,
    @Column(name="is_neutral")
    private boolean isNeutral;//     BOOLEAN      NULL,
    private String loadKey;//     VARCHAR(32)  NOT NULL

    @OneToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER, optional = true, orphanRemoval=true)
    @JoinColumn(name="id",referencedColumnName = "game_id")
    private Result result;

    protected Game() {
    }

    public Game(long seasonId, LocalDate date, LocalDateTime time, long homeTeamId, long awayTeamId, String location, boolean isNeutral, String loadKey) {
        this.id=0L;
        this.seasonId = seasonId;
        this.date = date;
        this.time = time;

        this.homeTeamId = homeTeamId;

        this.awayTeamId = awayTeamId;
        this.location = location;

        this.isNeutral = isNeutral;
        this.loadKey = loadKey;

        this.result=null;
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

    public long getHomeTeamId() {
        return homeTeamId;
    }

    public long getAwayTeamId() {
        return awayTeamId;
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

    public Result getResult() {
        return result;
    }
}
