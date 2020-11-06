package com.fijimf.deepfijomega.scraping;

import com.fijimf.deepfijomega.entity.schedule.Game;
import com.fijimf.deepfijomega.entity.schedule.Result;

import java.util.Optional;

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
        return oldGame != null && newGame != null && oldGame.updatedNeeded(newGame);
    }

    public Game getUpdatedGame(){
        oldGame.setNeutral(newGame.isNeutral());
        oldGame.setDate(newGame.getDate());
        oldGame.setTime(newGame.getTime());
        oldGame.setLocation(newGame.getLocation());
        Optional<Result> oldGameResult = oldGame.getResult();
        Optional<Result> newGameResult = newGame.getResult();
        if (oldGameResult.isEmpty()){
            newGameResult.ifPresent(oldGame::setResult);
        } else {
            if (newGameResult.isEmpty()){
                oldGame.setResult(null);
            } else {
                Result oldResult = oldGameResult.get();
                Result newResult = newGameResult.get();
                oldResult.setHomeScore(newResult.getHomeScore());
                oldResult.setAwayScore(newResult.getAwayScore());
                oldResult.setNumPeriods(newResult.getNumPeriods());
            }
        }
        return oldGame;
    }
}
