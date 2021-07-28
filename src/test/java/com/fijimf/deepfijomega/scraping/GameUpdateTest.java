package com.fijimf.deepfijomega.scraping;

import com.fijimf.deepfijomega.entity.schedule.Game;
import com.fijimf.deepfijomega.entity.schedule.Result;
import com.fijimf.deepfijomega.entity.schedule.Team;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class GameUpdateTest {
    //TestData

    LocalDate mar11 = LocalDate.of(2021, 3, 11);
    LocalDate mar12 = LocalDate.of(2021, 3, 12);
    LocalDate mar13 = LocalDate.of(2021, 3, 13);
    LocalDate mar14 = LocalDate.of(2021, 3, 14);

    Team georgetown = new Team("georgetown", "Georgetown", "Hoyas", "", "", "");
    Team villanova = new Team("villanova", "Villanova", "Wildcats", "", "", "");
    Team stjohns = new Team("st-johns", "St. John's", "Red Storm", "", "", "");
    Team syracuse = new Team("syracuse", "Syracuse", "Orange", "", "", "");


    Game g1 = new Game(1, mar11, mar11.atTime(19, 0), georgetown, villanova, null, false, "20210311", LocalDateTime.now(), null);
    Game g1_dupe = new Game(1, mar11, mar11.atTime(19, 0), georgetown, villanova, null, false, "20210311", LocalDateTime.now(), null);
    Game g1_withNewTime = new Game(1, mar11, mar11.atTime(12, 0), georgetown, villanova, null, false, "20210311", LocalDateTime.now(), null);

    Game addResult(Game g, int hs, int as, int np) {
        Game game = new Game(g.getSeasonId(), g.getDate(), g.getTime(), g.getHomeTeam(), g.getAwayTeam(), g.getLocation(), g.isNeutral(), g.getLoadKey(), LocalDateTime.now(), null);
        game.setResult(new Result(game, hs, as, np, LocalDateTime.now()));
        return game;
    }

    Game setId(Game g, long id) {
        try {
            Class<?> clazz = Class.forName("com.fijimf.deepfijomega.entity.schedule.Game");
            Field field = clazz.getDeclaredField("id");
            field.setAccessible(true);
            field.set(g, id);
        } catch (IllegalAccessException | ClassNotFoundException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        return g;
    }

    @Test
    void fromNewGame() {
        GameUpdate gameUpdate = GameUpdate.fromNewGame(g1);
        assertThat(gameUpdate).extracting("newGame").isEqualTo(g1);
        assertThat(gameUpdate).extracting("oldGame").isNull();
        assertThat(gameUpdate).extracting("insert").isEqualTo(true);
        assertThat(gameUpdate).extracting("update").isEqualTo(false);
        assertThat(gameUpdate).extracting("delete").isEqualTo(false);
    }

    @Test
    void fromOldGame() {
        GameUpdate gameUpdate = GameUpdate.fromOldGame(g1);
        assertThat(gameUpdate).extracting("oldGame").isEqualTo(g1);
        assertThat(gameUpdate).extracting("newGame").isNull();
        assertThat(gameUpdate).extracting("insert").isEqualTo(false);
        assertThat(gameUpdate).extracting("update").isEqualTo(false);
        assertThat(gameUpdate).extracting("delete").isEqualTo(true);
    }

    @Test
    void withOldGame1() {
        Game oldGame = setId(g1_dupe, 99);
        GameUpdate gameUpdate = GameUpdate.fromNewGame(g1).withOldGame(oldGame);
        assertThat(gameUpdate).extracting("newGame").isEqualTo(g1);
        assertThat(gameUpdate).extracting("oldGame").isEqualTo(oldGame);
        assertThat(gameUpdate).extracting("insert").isEqualTo(false);
        assertThat(gameUpdate).extracting("update").isEqualTo(false);
        assertThat(gameUpdate).extracting("delete").isEqualTo(false);
    }

    @Test
    void withOldGame2() {
        Game oldGame = setId(g1_withNewTime, 99);
        GameUpdate gameUpdate = GameUpdate.fromNewGame(g1).withOldGame(oldGame);
        assertThat(gameUpdate).extracting("newGame").isEqualTo(g1);
        assertThat(gameUpdate).extracting("oldGame").isEqualTo(oldGame);
        assertThat(gameUpdate).extracting("insert").isEqualTo(false);
        assertThat(gameUpdate).extracting("update").isEqualTo(true);
        assertThat(gameUpdate).extracting("delete").isEqualTo(false);
    }

    @Test
    void withOldGame3() {
        Game oldGame = setId(g1_dupe, 99);
        Game newGame = addResult(g1, 1, 99, 3);
        GameUpdate gameUpdate = GameUpdate.fromNewGame(newGame).withOldGame(oldGame);
        assertThat(gameUpdate).extracting("newGame").isEqualTo(newGame);
        assertThat(gameUpdate).extracting("oldGame").isEqualTo(oldGame);
        assertThat(gameUpdate).extracting("insert").isEqualTo(false);
        assertThat(gameUpdate).extracting("update").isEqualTo(true);
        assertThat(gameUpdate).extracting("delete").isEqualTo(false);
    }

    @Test
    void getUpdatedGame() {
        Game oldGame = setId(g1_dupe, 99);
        Game newGame = addResult(g1, 1, 99, 3);
        GameUpdate gameUpdate = GameUpdate.fromNewGame(newGame).withOldGame(oldGame);
        Game updatedGame = gameUpdate.getUpdatedGame();
        assertThat(updatedGame).extracting("id").isEqualTo(oldGame.getId());
        assertThat(updatedGame).isEqualToIgnoringGivenFields(newGame, "id", "updatedAt");
    }
}