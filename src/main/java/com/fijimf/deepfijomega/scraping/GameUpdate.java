package com.fijimf.deepfijomega.scraping;

import com.fijimf.deepfijomega.entity.schedule.Game;
import com.fijimf.deepfijomega.entity.schedule.Result;

import java.util.Objects;

public class GameUpdate {
    private final GameKey key;
    private final Game newGame;
    private final Game oldGame;

    public GameUpdate(GameKey key, Game newGame, Game oldGame) {
        this.key = key;
        this.newGame = newGame;
        this.oldGame = oldGame;
    }

    public static GameUpdate fromNewGame(Game g) {
        return new GameUpdate(GameKey.of(g), g, null);
    }

    public static GameUpdate fromOldGame(Game g) {
        return new GameUpdate(GameKey.of(g), null, g);
    }

    public GameUpdate withOldGame(Game g) {
        if (g.getId() == 0) throw new IllegalArgumentException("Attempt to add old game with id = 0");
        if (!GameKey.of(g).equals(key))
            throw new IllegalArgumentException("Attempt to add old game with mismatched game key");

        return new GameUpdate(key, newGame, g);
    }

    public Game getNewGame() {
        return newGame;
    }

    public Game getOldGame() {
        return oldGame;
    }

    public boolean isInsert() {
        return oldGame == null && newGame != null;
    }

    public boolean isDelete() {
        return oldGame != null && newGame == null;
    }

    public boolean isUpdate() {
        return oldGame != null && newGame != null && updateNeeded(oldGame,newGame);
    }

    private boolean updateNeeded(Game oldGame, Game newGame) {
        return oldGame.updatedNeeded(newGame);
    }

    public Game getUpdatedGame(Game oldGame, Game newGame){
        oldGame.setNeutral(newGame.isNeutral());
        oldGame.setDate(newGame.getDate());
        oldGame.setTime(newGame.getTime());
        oldGame.setLocation(newGame.getLocation());
        if (oldGame.getResult().isEmpty()){
            if (newGame.getResult().isEmpty()){
                // Do nothing
            } else {
                oldGame.setResult(newGame.getResult().get());
            }
        } else {
            if (newGame.getResult().isEmpty()){
                oldGame.setResult(null);
            } else {
                Result oldResult = oldGame.getResult().get();
                Result newResult = newGame.getResult().get();
                oldResult.setHomeScore(newResult.getHomeScore());
                oldResult.setAwayScore(newResult.getAwayScore());
                oldResult.setNumPeriods(newResult.getNumPeriods());
            }
        }
        return oldGame;
    }
}
