package com.fijimf.deepfijomega.entity.schedule;

import javax.persistence.*;

/*CREATE TABLE result
(
    id          BIGSERIAL PRIMARY KEY,
    game_id     BIGINT    NOT NULL REFERENCES game(id),
    home_score  INT       NOT NULL,
    away_score  INT       NOT NULL,
    num_periods INT       NOT NULL
);

CREATE UNIQUE INDEX on result (game_id);
*/
@Entity
@Table(name = "result")
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;//         BIGSERIAL PRIMARY KEY,
    @Column(name="game_id")
    private long gameId;//    BIGINT    NOT NULL REFERENCES game(id),
    @Column(name="home_score")
    private int homeScore;// INT       NOT NULL,
    @Column(name="away_score")
    private int awayScore;// INT       NOT NULL,
    @Column(name="num_periods")
    private int numPeriods;//INT       NOT NULL

    protected Result() {
    }

    public Result(long gameId, int homeScore, int awayScore, int numPeriods) {
        this.id=0L;
        this.gameId = gameId;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.numPeriods = numPeriods;
    }

    public long getId() {
        return id;
    }

    public long getGameId() {
        return gameId;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public int getNumPeriods() {
        return numPeriods;
    }
}
