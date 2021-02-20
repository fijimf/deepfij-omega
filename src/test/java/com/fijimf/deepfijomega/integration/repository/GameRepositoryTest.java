package com.fijimf.deepfijomega.integration.repository;


import com.fijimf.deepfijomega.entity.schedule.Game;
import com.fijimf.deepfijomega.entity.schedule.Result;
import com.fijimf.deepfijomega.entity.schedule.Season;
import com.fijimf.deepfijomega.entity.schedule.Team;
import com.fijimf.deepfijomega.integration.utility.DockerPostgresDb;
import com.fijimf.deepfijomega.repository.GameRepository;
import com.fijimf.deepfijomega.repository.SeasonRepository;
import com.fijimf.deepfijomega.repository.TeamRepository;
import com.spotify.docker.client.exceptions.DockerException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EntityScan(basePackages = {"com.fijimf.deepfijomega.entity"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)

public class GameRepositoryTest {
    private static final DockerPostgresDb dockerDb = new DockerPostgresDb("postgres:13-alpine", 59432);

    @Autowired
    private SeasonRepository seasonRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private GameRepository gameRepository;

    @PersistenceContext
    private EntityManager em;


    @BeforeAll
    public static void spinUpDatabase() throws DockerException, InterruptedException {
        dockerDb.spinUpDatabase();
    }

    @AfterAll
    public static void spinDownDb() throws DockerException, InterruptedException {
        dockerDb.spinDownDb();
    }

    @Test
    public void insertGameWithoutResult() {
        Season season = seasonRepository.findFirstByYear(2020).orElseThrow();
        Team gu = teamRepository.findFirstByKey("georgetown").orElseThrow();
        Team su = teamRepository.findFirstByKey("syracuse").orElseThrow();

        LocalDate d = LocalDate.of(2020, 2, 5);
        Game g = gameRepository.save(new Game(season.getId(), d, d.atTime(19, 30), gu, su, "Carrier Dome", false, "Test", null));
        assertThat(g.getId()).isGreaterThan(0L);
        assertThat(g.getResult()).isEmpty();

        Game g1 = gameRepository.findById(g.getId()).orElseThrow();
        assertThat(g1.getHomeTeam()).isEqualTo(gu);
        assertThat(g1.getAwayTeam()).isEqualTo(su);
        em.refresh(season);

        Season s1 = seasonRepository.findFirstByYear(2020).orElseThrow();
        assertThat(s1.getGames()).contains(g1);
    }

    @Test
    public void insertGameWithResult() {
        Season season = seasonRepository.findFirstByYear(2020).orElseThrow();
        Team gu = teamRepository.findFirstByKey("georgetown").orElseThrow();
        Team su = teamRepository.findFirstByKey("syracuse").orElseThrow();

        LocalDate d = LocalDate.of(2020, 2, 5);
        Result r = new Result(null, 99, 23, 2);
        Game og = new Game(season.getId(), d, d.atTime(19, 30), gu, su, "Carrier Dome", false, "Test", r);
        Game g = gameRepository.save(og);

        assertThat(g.getId()).isGreaterThan(0L);
        assertThat(g.getResult()).isPresent();
        assertThat(g.getResult().orElseThrow().getId()).isGreaterThan(0L);

        Game g1 = gameRepository.findById(g.getId()).orElseThrow();
        assertThat(g1.getHomeTeam()).isEqualTo(gu);
        assertThat(g1.getAwayTeam()).isEqualTo(su);
        assertThat(g.getResult()).isPresent();
    }

    @Test
    public void addNewResult() {
        Season season = seasonRepository.findFirstByYear(2020).orElseThrow();
        Team gu = teamRepository.findFirstByKey("georgetown").orElseThrow();
        Team su = teamRepository.findFirstByKey("syracuse").orElseThrow();

        LocalDate d = LocalDate.of(2020, 2, 5);

        Game og = new Game(season.getId(), d, d.atTime(19, 30), gu, su, "Carrier Dome", false, "Test", null);
        Game g = gameRepository.save(og);

        assertThat(g.getId()).isGreaterThan(0L);
        assertThat(g.getResult()).isEmpty();

        Game g1 = gameRepository.findById(g.getId()).orElseThrow();
        Result r2 = new Result(g1, 199, 83, 3);
        g1.setResult(r2);
        gameRepository.save(g1);

        Game g2 = gameRepository.findById(g.getId()).orElseThrow();
        assertThat(g2.getResult().orElseThrow().getHomeScore()).isEqualTo(199);
    }

    @Test
    public void updateExistingResult() {
        Season season = seasonRepository.findFirstByYear(2020).orElseThrow();
        Team gu = teamRepository.findFirstByKey("georgetown").orElseThrow();
        Team su = teamRepository.findFirstByKey("syracuse").orElseThrow();

        LocalDate d = LocalDate.of(2020, 2, 5);
        Result r = new Result(null, 99, 23, 2);
        Game og = new Game(season.getId(), d, d.atTime(19, 30), gu, su, "Carrier Dome", false, "Test", r);
        Game g = gameRepository.save(og);

        assertThat(g.getId()).isGreaterThan(0L);
        assertThat(g.getResult()).isPresent();
        assertThat(g.getResult().orElseThrow().getId()).isGreaterThan(0L);

        Game g1 = gameRepository.findById(g.getId()).orElseThrow();
        Result result = g1.getResult().orElseThrow();
        result.setAwayScore(44);
        result.setHomeScore(123);

        gameRepository.save(g1);

        Game g2 = gameRepository.findById(g.getId()).orElseThrow();
        assertThat(g2.getHomeTeam()).isEqualTo(gu);
        assertThat(g2.getAwayTeam()).isEqualTo(su);
        assertThat(g2.getResult()).isPresent();
        assertThat(g2.getResult().orElseThrow().getHomeScore()).isEqualTo(123);
    }

    @Test
    public void deleteExistingResult() {
        Season season = seasonRepository.findFirstByYear(2020).orElseThrow();
        Team gu = teamRepository.findFirstByKey("georgetown").orElseThrow();
        Team su = teamRepository.findFirstByKey("syracuse").orElseThrow();

        LocalDate d = LocalDate.of(2020, 2, 5);
        Result r = new Result(null, 99, 23, 2);
        Game og = new Game(season.getId(), d, d.atTime(19, 30), gu, su, "Carrier Dome", false, "Test", r);
        Game g = gameRepository.save(og);

        assertThat(g.getId()).isGreaterThan(0L);
        assertThat(g.getResult()).isPresent();
        assertThat(g.getResult().orElseThrow().getId()).isGreaterThan(0L);

        Game g1 = gameRepository.findById(g.getId()).orElseThrow();
        g1.setResult(null);

        gameRepository.save(g1);

        Game g2 = gameRepository.findById(g.getId()).orElseThrow();
        assertThat(g2.getHomeTeam()).isEqualTo(gu);
        assertThat(g2.getAwayTeam()).isEqualTo(su);
        assertThat(g2.getResult()).isEmpty();
    }

}
