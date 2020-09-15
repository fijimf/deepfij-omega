package com.fijimf.deepfijomega.scraping;

import com.fijimf.deepfijomega.entity.schedule.*;
import com.fijimf.deepfijomega.repository.AliasRepository;
import com.fijimf.deepfijomega.repository.GameRepository;
import com.fijimf.deepfijomega.repository.SeasonRepository;
import com.fijimf.deepfijomega.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ScheduleUpdater {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleUpdater.class);
    private final TeamRepository teamRepository;
    private final GameRepository gameRepository;
    private final AliasRepository aliasRepository;
    private final SeasonRepository seasonRepository;

    @Autowired
    public ScheduleUpdater(TeamRepository teamRepository, GameRepository gameRepository, AliasRepository aliasRepository, SeasonRepository seasonRepository) {
        this.teamRepository = teamRepository;
        this.gameRepository = gameRepository;
        this.aliasRepository = aliasRepository;
        this.seasonRepository = seasonRepository;
    }


    public Optional<Team> findTeam(String key) {
        return teamRepository
                .findFirstByKey(key)
                .or(() -> aliasRepository
                        .findFirstByAlias(key)
                        .map(Alias::getTeam)
                );
    }

    public Optional<Season> findSeason(LocalDate localDate) {
        for (Season s : seasonRepository.findAll()) {
            if (s.inSeason(localDate)) {
                return Optional.ofNullable(s);
            }
        }
        return Optional.empty();
    }

    public Optional<Game> createGame(UpdateCandidate u, String loadKey) {
        Optional<Result> optionalResult = createResult(u);
        Optional<Game> optionalGame = findTeam(u.getHomeKey()).flatMap(
                homeTeam -> findTeam(u.getAwayKey()).flatMap(
                        awayTeam -> findSeason(u.getDate()).map(season -> {
                            Game gg = new Game(season.getId(), u.getDate(), u.getDateTime(), homeTeam, awayTeam, null, false, loadKey, null);
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
                        o -> GameUpdate.fromNewGame(o.get()))
                );

        gameRepository.findAllByLoadKey(key).forEach(g -> {
            GameKey gk = GameKey.of(g);
            gameUpdates.put(gk, gameUpdates.containsKey(gk) ? gameUpdates.get(gk).withOldGame(g) : GameUpdate.fromOldGame(g));
        });

        int numInserts = 0;
        int numUpdates = 0;
        int numDeletes = 0;
        int numUnchanged = 0;
        for (GameUpdate u : gameUpdates.values()) {
            if (u.isInsert()) {
                gameRepository.save(u.getNewGame());
                numInserts++;
            } else if (u.isDelete()) {
                gameRepository.deleteById(u.getOldGame().getId());
                numDeletes++;
            } else if (u.isUpdate()) {
                gameRepository.save(u.getOldGame());
                numUpdates++;
            } else {
                numUnchanged++;
            }

        }
        logger.info(String.format("For load key %s, got %d update candidates.", key, updateCandidates.size()));
        logger.info(String.format("Update candidates generated %d inserts, %d updates and %d deletes",
                numInserts, numUpdates, numDeletes));
        return new UpdateResult(updateCandidates.size(), updateCandidates.size() - (gameUpdates.size()-numDeletes), numInserts, numUpdates, numDeletes, numUnchanged);
    }

/*
    def updateGamesAndResults(updates:List[UpdateCandidate], loadKey:String):F[List[(Game,Option[Result])]]=

    {
        for {
        updatesWithKeys< -findKeys(updates, loadKey)
        _< -F.delay(log.info(s"For $loadKey, update candidates generated ${updatesWithKeys.size} games to update."))
        gamesWithKeys< -loadGamesAndResults(loadKey)
        _< -F.delay(log.info(s"For $loadKey, ${gamesWithKeys.size} existing games were loaded."))
        mods< -doUpdates(updatesWithKeys, gamesWithKeys, loadKey)
        _< -F.delay(log.info(s"For $loadKey, ${mods.size} changes were made."))
    } yield {
        mods
    }
    }


    def doUpdates(updatesWithKeys:Map[GameKey, (Game, Option[Result])],gamesWithKeys:Map[GameKey,(Game,Option[Result])],loadKey:String):F[List[(Game,Option[Result])]]=

    {
        val updateKeys:Set[GameKey] = updatesWithKeys.keySet
        val existingKeys:Set[GameKey] = gamesWithKeys.keySet

        val inserts:Set[GameKey] = updateKeys.diff(existingKeys)
        val deletes:Set[GameKey] = existingKeys.diff(updateKeys)
        val updates:Set[GameKey] = updateKeys.intersect(existingKeys)
        log.info(s"${inserts.size} inserts, ${deletes.size} deletes, ${updates.size} updates ")
        inserts.foreach(println(_))
        for {
        is< -inserts.flatMap(k = > updatesWithKeys.get(k)).map((insert _).tupled).toList.sequence
        ds< -deletes.flatMap(k = > gamesWithKeys.get(k)).map((delete _).tupled).toList.sequence
        us< -( for {
            k< -updates
            n< -updatesWithKeys.get(k)
            o< -gamesWithKeys.get(k) if updateNeeded(o, n)
        } yield {
            update(o._1, n._1, o._2, n._2)
        }).toList.sequence
    } yield {
        is++ ds++ us
    }
    }

    def updateNeeded(oldPair:(Game, Option[Result]),newPair:(Game,Option[Result])):Boolean =

    {
        val(oldGame, oldRes) = oldPair
        val(newGame, newRes) = newPair
        val augmentedGame:Game = newGame.copy(id = oldGame.id)
        val augmentedRes:Option[Result] = newRes.map(_.copy(
            id = oldRes.map(_.id).getOrElse(0),
            gameId = oldRes.map(_.gameId).getOrElse(0)
    ))
        !(oldGame == = augmentedGame && oldRes == = augmentedRes)
    }


    def insert(game:Game, res:Option[Result]):F[(Game,Option[Result])]=

    {
    import GameDao._
        for {
        g:
        Game< -GameDao.insert(game).withUniqueGeneratedKeys[Game] (GameDao.cols:_ *).transact(xa)
        o:
        Option[Result] < -res match {
            case Some(r) =>
                ResultDao.insert(r.copy(gameId = g.id))
                        .withUniqueGeneratedKeys[Result] (ResultDao.cols:_ *)
            .transact(xa)
                    .map(Option(_))
            case None =>
                F.pure(Option.empty[Result])
        }
    } yield {
        (g, o)
    }
    }

    def delete(game:Game, res:Option[Result]):F[(Game,Option[Result])]=

    {
        for {
        _< -ResultDao.deleteByGameId(game.id).run.transact(xa)
        _< -GameDao.delete(game.id).run.transact(xa)
    } yield {
        (game, res)
    }
    }

    def update(g:Game, h:Game, o:Option[Result], p:Option[Result]):F[(Game,Option[Result])]=

    {
        for {
        gg< -gameUpdate(g, h.copy(id = g.id))
        oo< -optResultUpdate(gg.id, o, p)
    } yield {
        (gg, oo)
    }
    }

    private def gameUpdate(g:Game, h1:Game):F[Game]=

    {
    import GameDao._
        for {
        h2< -GameDao.update(h1).withUniqueGeneratedKeys[Game] (GameDao.cols:_ *).transact(xa)
    } yield {
        h2
    }
    }

    private def optResultUpdate(gameId:Long, o:Option[Result], p:Option[Result]):F[Option[Result]]=

    {
        (o, p)match {
        case (Some(o1), Some(p1)) =>
            ResultDao.update(p1.copy(id = o1.id, gameId = o1.gameId)).withUniqueGeneratedKeys[Result] (ResultDao.cols:_ *).transact(xa).map(Some(_))
        case (Some(o1), None) =>
            ResultDao.delete(o1.id).run.transact(xa).map(_ = > Option.empty[Result])
        case (None, Some(p1)) =>
            ResultDao.insert(p1.copy(gameId = gameId)).withUniqueGeneratedKeys[Result] (ResultDao.cols:_ *).transact(xa).map(Some(_))
        case (None, None) =>F.pure(None)
    }
    }

    def findKeys(pgs:List[UpdateCandidate], loadKey:String):F[Map[GameKey,(Game,Option[Result])]]=

    {
        pgs.map(findKeysForGame(_, loadKey)).sequence.map(_.flatten.toMap)
    }

    def findKeysForGame(pg:UpdateCandidate, loadKey:String):F[List[(GameKey,(Game,Option[Result]))]]=

    {
        ( for {
        ht< -loadTeam(pg.homeKey)
        at< -loadTeam(pg.awayKey)
        s< -findSeason(pg.dateTime)
        key = GameKey(pg.date.toEpochDay, s.id, ht.id, at.id)
    } yield {
        log.info(s"$pg => $key")
        key -> (pg.toGame(0L, key, loadKey), pg.toOptionResult(0L, 0L))
    }).value.map(_.toList)
    }

    def loadGamesAndResults(loadKey:String):F[Map[GameKey,(Game,Option[Result])]]=

    {
        for {
        gs< -GameDao.findByLoadKey(loadKey).to[List].transact(xa)
    } yield {
        gs.map(t = > {
                val key:GameKey = GameKey(t._1.date.toEpochDay, t._1.seasonId, t._1.homeTeamId, t._1.awayTeamId)
        log.info(s"=> $key")
        key -> t
      }).toMap
    }
    }

    def loadTeam(key:String):OptionT[F,Team]=

    {
        OptionT[F, Team](TeamDao.findByKey(key).option.transact(xa))
            .orElseF(TeamDao.findByAlias(key).option.transact(xa))
    }

    def findSeason(date:LocalDateTime):OptionT[F,Season]=

    OptionT(for {
        s< -SeasonDao.findByDate(date).option.transact(xa)
    }

    yield {
        s
    })
*/
}
