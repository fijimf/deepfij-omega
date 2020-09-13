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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;//         BIGSERIAL PRIMARY KEY,

    @OneToOne(fetch=FetchType.LAZY, optional = false)
    @JoinColumn(name="game_id",nullable = false )
    private Game game;
    @Column(name="home_score")
    private int homeScore;// INT       NOT NULL,
    @Column(name="away_score")
    private int awayScore;// INT       NOT NULL,
    @Column(name="num_periods")
    private int numPeriods;//INT       NOT NULL

    protected Result() {
    }

    public Result(Game game, int homeScore, int awayScore, int numPeriods) {
        this.id=0L;
        this.game = game;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.numPeriods = numPeriods;
    }

    public long getId() {
        return id;
    }

    public Game getGame() {
        return game;
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

    public void setGame(Game game) {
        this.game=game;
    }

    public void setHomeScore(int homeScore) {
        this.homeScore = homeScore;
    }

    public void setAwayScore(int awayScore) {
        this.awayScore = awayScore;
    }

    public void setNumPeriods(int numPeriods) {
        this.numPeriods = numPeriods;
    }

    public boolean updateNeeded(Result result){
        return homeScore!=result.homeScore ||
         awayScore!=result.awayScore ||
         numPeriods!=result.numPeriods ;
    }
}
