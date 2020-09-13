package com.fijimf.deepfijomega.integration.scraping;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fijimf.deepfijomega.entity.schedule.Game;
import com.fijimf.deepfijomega.entity.schedule.Result;
import com.fijimf.deepfijomega.entity.schedule.Season;
import com.fijimf.deepfijomega.entity.schedule.Team;
import com.fijimf.deepfijomega.integration.utility.DockerPostgresDb;
import com.fijimf.deepfijomega.repository.AliasRepository;
import com.fijimf.deepfijomega.repository.GameRepository;
import com.fijimf.deepfijomega.repository.SeasonRepository;
import com.fijimf.deepfijomega.repository.TeamRepository;
import com.fijimf.deepfijomega.scraping.Casablanca;
import com.fijimf.deepfijomega.scraping.ScheduleUpdater;
import com.fijimf.deepfijomega.scraping.UpdateCandidate;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EntityScan(basePackages = {"com.fijimf.deepfijomega.entity"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)

public class ScheduleUpdaterTest {
    private static final DockerPostgresDb dockerDb = new DockerPostgresDb("postgres:13-alpine", 59432);

    @Autowired
    private AliasRepository aliasRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private SeasonRepository seasonRepository;
    @Autowired
    private GameRepository gameRepository;


    @BeforeAll
    public static void spinUpDatabase() throws DockerCertificateException, DockerException, InterruptedException {
        dockerDb.spinUpDatabase();
    }

    @AfterAll
    public static void spinDownDb() throws DockerException, InterruptedException {
        dockerDb.spinDownDb();
    }

    @Test
    public void scrapeGamesAndUpdate() throws IOException {
        ScheduleUpdater scheduleUpdater = new ScheduleUpdater(teamRepository,gameRepository,aliasRepository,seasonRepository);

        InputStream inputStream = new ClassPathResource("eg1.json").getInputStream();
        ObjectMapper mapper = new ObjectMapper();
        Casablanca value = mapper.readValue(inputStream, Casablanca.class);
        List<UpdateCandidate> updateCandidates = value.extractUpdates();

        scheduleUpdater.updateGamesAndResults("Test", updateCandidates);

    }


}
