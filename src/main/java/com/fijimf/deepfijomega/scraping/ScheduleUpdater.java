package com.fijimf.deepfijomega.scraping;

import com.fijimf.deepfijomega.entity.schedule.*;
import com.fijimf.deepfijomega.manager.ScheduleManager;
import com.fijimf.deepfijomega.repository.AliasRepository;
import com.fijimf.deepfijomega.repository.GameRepository;
import com.fijimf.deepfijomega.repository.SeasonRepository;
import com.fijimf.deepfijomega.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

@Component
public class ScheduleUpdater {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleUpdater.class);
    private final TeamRepository teamRepository;
    private final GameRepository gameRepository;
    private final AliasRepository aliasRepository;
    private final SeasonRepository seasonRepository;
    private BinaryOperator<GameUpdate> updateMerge = (g, h) -> {
        logger.warn("Game update repeated " + g + " " + h);
        return g;
    };

    private final ScheduleManager schedMgr;

    @Autowired
    public ScheduleUpdater(TeamRepository teamRepository, GameRepository gameRepository, AliasRepository aliasRepository, SeasonRepository seasonRepository, ScheduleManager schedMgr) {
        this.teamRepository = teamRepository;
        this.gameRepository = gameRepository;
        this.aliasRepository = aliasRepository;
        this.seasonRepository = seasonRepository;
        this.schedMgr = schedMgr;
    }



    public Optional<Game> createGame(UpdateCandidate u, String loadKey) {
        Optional<Result> optionalResult = createResult(u);
        Optional<Game> optionalGame = schedMgr.findTeam(u.getHomeKey()).flatMap(
                homeTeam -> schedMgr.findTeam(u.getAwayKey()).flatMap(
                        awayTeam -> seasonRepository.findFirstByYear(Season.dateToSeasonYear(u.getDate())).map(season -> {
                            Game gg = new Game(season.getId(), u.getDate(), u.getDateTime(), homeTeam, awayTeam, u.getLocation().orElse(null), u.getIsNeutral().orElse(false), loadKey, null);
                            if (optionalResult.isPresent()) {
                                optionalResult.get().setGame(gg);
                                gg.setResult(optionalResult.get());
                            }
                            return gg;
                        })
                )
        );
        if (optionalGame.isEmpty()) {
            logger.info(String.format("Failed to find season or teams for (%s,%s,%s)",
                    u.getDate().toString(), u.getHomeKey(), u.getAwayKey()));
        }
        return optionalGame;
    }

    public Optional<Result> createResult(UpdateCandidate u) {
        return u.getHomeScore().flatMap(
                homeScore -> u.getAwayScore().flatMap(
                        awayScore -> u.getNumPeriods().map(
                                numPeriods -> new Result(null, homeScore, awayScore, numPeriods)
                        )
                )
        );
    }

    public UpdateResult updateGamesAndResults(String key, List<UpdateCandidate> updateCandidates) {
        Map<GameKey, GameUpdate> gameUpdates = updateCandidates
                .stream()
                .map(updateCandidate -> createGame(updateCandidate, key))
                .filter(Optional::isPresent)
                .collect(Collectors.toMap(
                        o -> GameKey.of(o.get()),
                        o -> GameUpdate.fromNewGame(o.get()),
                        updateMerge)
                );

        gameRepository.findAllByLoadKey(key).forEach(g -> {
            GameKey gk = GameKey.of(g);
            gameUpdates.put(gk, gameUpdates.containsKey(gk) ? gameUpdates.get(gk).withOldGame(g) : GameUpdate.fromOldGame(g));
        });

        int numInserts = 0;
        int numUpdates = 0;
        int numDeletes = 0;
        int numUnchanged = 0;
        for (Map.Entry<GameKey, GameUpdate> e : gameUpdates.entrySet()) {
            GameUpdate u = e.getValue();
            System.err.println(e.getKey());
            if (u.isInsert()) {
                gameRepository.save(u.getNewGame());
                numInserts++;
            } else if (u.isDelete()) {
                gameRepository.deleteById(u.getOldGame().getId());
                numDeletes++;
            } else if (u.isUpdate()) {
                gameRepository.save(u.getUpdatedGame());
                numUpdates++;
            } else {
                numUnchanged++;
            }

        }
        logger.info(String.format("For load key %s, got %d update candidates.", key, updateCandidates.size()));
        logger.info(String.format("Update candidates generated %d inserts, %d updates and %d deletes",
                numInserts, numUpdates, numDeletes));
        return new UpdateResult(updateCandidates.size(), updateCandidates.size() - (gameUpdates.size() - numDeletes), numInserts, numUpdates, numDeletes, numUnchanged);
    }
}
