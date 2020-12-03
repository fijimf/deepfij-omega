package com.fijimf.deepfijomega.integration.manager;


import com.fijimf.deepfijomega.integration.utility.DockerPostgresDb;
import com.fijimf.deepfijomega.manager.ScheduleManager;
import com.fijimf.deepfijomega.repository.*;
import com.spotify.docker.client.exceptions.DockerException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EntityScan(basePackages = {"com.fijimf.deepfijomega.entity"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ScheduleManagerTest {
    private static final DockerPostgresDb dockerDb = new DockerPostgresDb("postgres:13-alpine", 59432);

    @Autowired
    TeamRepository teamRepo;

    @Autowired
    ConferenceRepository conferenceRepo;

    @Autowired
    GameRepository gameRepo;

    @Autowired
    SeasonRepository seasonRepo;


    @BeforeAll
    public static void spinUpDatabase() throws DockerException, InterruptedException {
        dockerDb.spinUpDatabase();
    }

    @AfterAll
    public static void spinDownDb() throws DockerException, InterruptedException {
        dockerDb.spinDownDb();
    }

    @Test
    public void contextLoads() {
        ScheduleManager mgr = new ScheduleManager(teamRepo, conferenceRepo, gameRepo, seasonRepo);
        assertThat(mgr).isNotNull();
    }

    @Test
    public void listSeasons() {
        ScheduleManager mgr = new ScheduleManager(teamRepo, conferenceRepo, gameRepo, seasonRepo);
        assertThat(mgr.getSeasons())
                .isNotNull()
                .hasSize(7)
                .allSatisfy(s -> {
                    assertThat(s.getId()).isGreaterThan(0);
                    assertThat(s.getYear()).isGreaterThan(2000);
                    assertThat(s.getConferenceMapping()).isNotEmpty();
                    assertThat(s.getSeasonDates().size()).isGreaterThan(100);
                });
    }

    @Test
    public void currentSeason() {
        ScheduleManager mgr = new ScheduleManager(teamRepo, conferenceRepo, gameRepo, seasonRepo);
        assertThat(mgr.getCurrentSeason())
                .isPresent().hasValueSatisfying(s->{
                    assertThat(s.getId()).isGreaterThan(0);
                    assertThat(s.getYear()).isEqualTo(2021);
                    assertThat(s.getConferenceMapping()).isNotEmpty();
                    assertThat(s.getSeasonDates().size()).isGreaterThan(100);
                });
    }
}
