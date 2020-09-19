package com.fijimf.deepfijomega.integration.scraping;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fijimf.deepfijomega.entity.schedule.Game;
import com.fijimf.deepfijomega.integration.utility.DockerPostgresDb;
import com.fijimf.deepfijomega.repository.AliasRepository;
import com.fijimf.deepfijomega.repository.GameRepository;
import com.fijimf.deepfijomega.repository.SeasonRepository;
import com.fijimf.deepfijomega.repository.TeamRepository;
import com.fijimf.deepfijomega.scraping.Casablanca;
import com.fijimf.deepfijomega.scraping.ScheduleUpdater;
import com.fijimf.deepfijomega.scraping.UpdateCandidate;
import com.fijimf.deepfijomega.scraping.UpdateResult;
import com.spotify.docker.client.exceptions.DockerException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.io.InputStream;
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
    public static void spinUpDatabase() throws  DockerException, InterruptedException {
        dockerDb.spinUpDatabase();
    }

    @AfterAll
    public static void spinDownDb() throws DockerException, InterruptedException {
        dockerDb.spinDownDb();
    }

    @Test
    public void basicScrapeGamesAndUpdate() throws IOException {
        ScheduleUpdater scheduleUpdater = new ScheduleUpdater(teamRepository, gameRepository, aliasRepository, seasonRepository);

        assertThat(gameRepository.findAll()).hasSize(0);
        InputStream inputStream = new ClassPathResource("eg1.json").getInputStream();
        ObjectMapper mapper = new ObjectMapper();
        Casablanca value = mapper.readValue(inputStream, Casablanca.class);
        List<UpdateCandidate> updateCandidates = value.extractUpdates();

        UpdateResult updateResult = scheduleUpdater.updateGamesAndResults("Test", updateCandidates);
        assertThat(gameRepository.findAll()).hasSize(15);
        assertThat(updateResult.getUnmapped()).isEqualTo(0);
        assertThat(updateResult.getInserts()).isEqualTo(15);
        assertThat(updateResult.getUpdates()).isEqualTo(0);
        assertThat(updateResult.getDeletes()).isEqualTo(0);
        assertThat(updateResult.getUnchanged()).isEqualTo(0);
    }

    @Test
    public void scrapeGamesAndUpdateIsIdempotent() throws IOException {
        ScheduleUpdater scheduleUpdater = new ScheduleUpdater(teamRepository, gameRepository, aliasRepository, seasonRepository);

        assertThat(gameRepository.findAll()).hasSize(0);
        InputStream inputStream = new ClassPathResource("eg1.json").getInputStream();
        ObjectMapper mapper = new ObjectMapper();
        Casablanca value = mapper.readValue(inputStream, Casablanca.class);
        List<UpdateCandidate> updateCandidates = value.extractUpdates();

        UpdateResult updateResult1 = scheduleUpdater.updateGamesAndResults("Test", updateCandidates);
        assertThat(gameRepository.findAll()).hasSize(15);
        assertThat(updateResult1.getUnmapped()).isEqualTo(0);
        assertThat(updateResult1.getInserts()).isEqualTo(15);
        assertThat(updateResult1.getUpdates()).isEqualTo(0);
        assertThat(updateResult1.getDeletes()).isEqualTo(0);
        assertThat(updateResult1.getUnchanged()).isEqualTo(0);

        UpdateResult updateResult2 = scheduleUpdater.updateGamesAndResults("Test", updateCandidates);
        assertThat(gameRepository.findAll()).hasSize(15);
        assertThat(updateResult2.getUnmapped()).isEqualTo(0);
        assertThat(updateResult2.getInserts()).isEqualTo(0);
        assertThat(updateResult2.getUpdates()).isEqualTo(0);
        assertThat(updateResult2.getDeletes()).isEqualTo(0);
        assertThat(updateResult2.getUnchanged()).isEqualTo(15);
    }

    @Test
    public void basicScrapeGamesAndUpdateWithUnmappedTeam() throws IOException {
        ScheduleUpdater scheduleUpdater = new ScheduleUpdater(teamRepository, gameRepository, aliasRepository, seasonRepository);

        assertThat(gameRepository.findAll()).hasSize(0);
        InputStream inputStream = new ClassPathResource("eg2.json").getInputStream();
        ObjectMapper mapper = new ObjectMapper();
        Casablanca value = mapper.readValue(inputStream, Casablanca.class);
        List<UpdateCandidate> updateCandidates = value.extractUpdates();

        UpdateResult updateResult = scheduleUpdater.updateGamesAndResults("Test", updateCandidates);
        assertThat(gameRepository.findAll()).hasSize(14);
        assertThat(updateResult.getUnmapped()).isEqualTo(1);
        assertThat(updateResult.getInserts()).isEqualTo(14);
        assertThat(updateResult.getUpdates()).isEqualTo(0);
        assertThat(updateResult.getDeletes()).isEqualTo(0);
        assertThat(updateResult.getUnchanged()).isEqualTo(0);
    }

    @Test
    public void basicUpdatePartialThenFull() throws IOException {
        ScheduleUpdater scheduleUpdater = new ScheduleUpdater(teamRepository, gameRepository, aliasRepository, seasonRepository);

        assertThat(gameRepository.findAll()).hasSize(0);
        ObjectMapper mapper = new ObjectMapper();

        InputStream inputStream1 = new ClassPathResource("eg1_half_complete.json").getInputStream();
        Casablanca value1 = mapper.readValue(inputStream1, Casablanca.class);
        List<UpdateCandidate> updateCandidates1 = value1.extractUpdates();
        InputStream inputStream2 = new ClassPathResource("eg1.json").getInputStream();
        Casablanca value2 = mapper.readValue(inputStream2, Casablanca.class);
        List<UpdateCandidate> updateCandidates2 = value2.extractUpdates();

        UpdateResult updateResult1 = scheduleUpdater.updateGamesAndResults("Test", updateCandidates1);
        assertThat(gameRepository.findAll()).hasSize(8);
        assertThat(updateResult1.getUnmapped()).isEqualTo(0);
        assertThat(updateResult1.getInserts()).isEqualTo(8);
        assertThat(updateResult1.getUpdates()).isEqualTo(0);
        assertThat(updateResult1.getDeletes()).isEqualTo(0);
        assertThat(updateResult1.getUnchanged()).isEqualTo(0);

        UpdateResult updateResult2 = scheduleUpdater.updateGamesAndResults("Test", updateCandidates2);
        assertThat(gameRepository.findAll()).hasSize(15);
        assertThat(updateResult2.getUnmapped()).isEqualTo(0);
        assertThat(updateResult2.getInserts()).isEqualTo(7);
        assertThat(updateResult2.getUpdates()).isEqualTo(0);
        assertThat(updateResult2.getDeletes()).isEqualTo(0);
        assertThat(updateResult2.getUnchanged()).isEqualTo(8);
    }

    @Test
    public void basicUpdateFullThenPartial() throws IOException {
        ScheduleUpdater scheduleUpdater = new ScheduleUpdater(teamRepository, gameRepository, aliasRepository, seasonRepository);

        assertThat(gameRepository.findAll()).hasSize(0);
        ObjectMapper mapper = new ObjectMapper();

        InputStream inputStream1 = new ClassPathResource("eg1.json").getInputStream();
        Casablanca value1 = mapper.readValue(inputStream1, Casablanca.class);
        List<UpdateCandidate> updateCandidates1 = value1.extractUpdates();
        InputStream inputStream2 = new ClassPathResource("eg1_half_complete.json").getInputStream();
        Casablanca value2 = mapper.readValue(inputStream2, Casablanca.class);
        List<UpdateCandidate> updateCandidates2 = value2.extractUpdates();

        UpdateResult updateResult1 = scheduleUpdater.updateGamesAndResults("Test", updateCandidates1);
        assertThat(gameRepository.findAll()).hasSize(15);
        assertThat(updateResult1.getUnmapped()).isEqualTo(0);
        assertThat(updateResult1.getInserts()).isEqualTo(15);
        assertThat(updateResult1.getUpdates()).isEqualTo(0);
        assertThat(updateResult1.getDeletes()).isEqualTo(0);
        assertThat(updateResult1.getUnchanged()).isEqualTo(0);

        UpdateResult updateResult2 = scheduleUpdater.updateGamesAndResults("Test", updateCandidates2);
        assertThat(gameRepository.findAll()).hasSize(8);
        assertThat(updateResult2.getUnmapped()).isEqualTo(0);
        assertThat(updateResult2.getInserts()).isEqualTo(0);
        assertThat(updateResult2.getUpdates()).isEqualTo(0);
        assertThat(updateResult2.getDeletes()).isEqualTo(7);
        assertThat(updateResult2.getUnchanged()).isEqualTo(8);
    }
    @Test
    public void basicUpdateAddResults() throws IOException {
        ScheduleUpdater scheduleUpdater = new ScheduleUpdater(teamRepository, gameRepository, aliasRepository, seasonRepository);

        assertThat(gameRepository.findAll()).hasSize(0);
        ObjectMapper mapper = new ObjectMapper();

        InputStream inputStream1 = new ClassPathResource("eg1_no_finals.json").getInputStream();
        Casablanca value1 = mapper.readValue(inputStream1, Casablanca.class);
        List<UpdateCandidate> updateCandidates1 = value1.extractUpdates();
        InputStream inputStream2 = new ClassPathResource("eg1.json").getInputStream();
        Casablanca value2 = mapper.readValue(inputStream2, Casablanca.class);
        List<UpdateCandidate> updateCandidates2 = value2.extractUpdates();

        UpdateResult updateResult1 = scheduleUpdater.updateGamesAndResults("Test", updateCandidates1);
        Iterable<Game> games1 = gameRepository.findAll();
        assertThat(games1).hasSize(15);
        assertThat(games1).allSatisfy(g->assertThat(g.getResult()).isEmpty());
        assertThat(updateResult1.getUnmapped()).isEqualTo(0);
        assertThat(updateResult1.getInserts()).isEqualTo(15);
        assertThat(updateResult1.getUpdates()).isEqualTo(0);
        assertThat(updateResult1.getDeletes()).isEqualTo(0);
        assertThat(updateResult1.getUnchanged()).isEqualTo(0);

        UpdateResult updateResult2 = scheduleUpdater.updateGamesAndResults("Test", updateCandidates2);
        Iterable<Game> g2 = gameRepository.findAll();
        assertThat(g2).hasSize(15);
        assertThat(updateResult2.getUnmapped()).isEqualTo(0);
        assertThat(updateResult2.getInserts()).isEqualTo(0);
        assertThat(updateResult2.getUpdates()).isEqualTo(15);
        assertThat(updateResult2.getDeletes()).isEqualTo(0);
        assertThat(updateResult2.getUnchanged()).isEqualTo(0);
        assertThat(g2).allSatisfy(g->assertThat(g.getResult()).isPresent());
    }

    @Test
    public void basicUpdateRemoveResults() throws IOException {
        ScheduleUpdater scheduleUpdater = new ScheduleUpdater(teamRepository, gameRepository, aliasRepository, seasonRepository);

        assertThat(gameRepository.findAll()).hasSize(0);
        ObjectMapper mapper = new ObjectMapper();

        InputStream inputStream1 = new ClassPathResource("eg1.json").getInputStream();
        Casablanca value1 = mapper.readValue(inputStream1, Casablanca.class);
        List<UpdateCandidate> updateCandidates1 = value1.extractUpdates();
        InputStream inputStream2 = new ClassPathResource("eg1_no_finals.json").getInputStream();
        Casablanca value2 = mapper.readValue(inputStream2, Casablanca.class);
        List<UpdateCandidate> updateCandidates2 = value2.extractUpdates();

        UpdateResult updateResult1 = scheduleUpdater.updateGamesAndResults("Test", updateCandidates1);
        Iterable<Game> games1 = gameRepository.findAll();
        assertThat(games1).hasSize(15);
        assertThat(games1).allSatisfy(g->assertThat(g.getResult()).isPresent());
        assertThat(updateResult1.getUnmapped()).isEqualTo(0);
        assertThat(updateResult1.getInserts()).isEqualTo(15);
        assertThat(updateResult1.getUpdates()).isEqualTo(0);
        assertThat(updateResult1.getDeletes()).isEqualTo(0);
        assertThat(updateResult1.getUnchanged()).isEqualTo(0);

        UpdateResult updateResult2 = scheduleUpdater.updateGamesAndResults("Test", updateCandidates2);
        Iterable<Game> g2 = gameRepository.findAll();
        assertThat(g2).hasSize(15);
        assertThat(updateResult2.getUnmapped()).isEqualTo(0);
        assertThat(updateResult2.getInserts()).isEqualTo(0);
        assertThat(updateResult2.getUpdates()).isEqualTo(15);
        assertThat(updateResult2.getDeletes()).isEqualTo(0);
        assertThat(updateResult2.getUnchanged()).isEqualTo(0);
        assertThat(g2).allSatisfy(g->assertThat(g.getResult()).isEmpty());
    }
 @Test
    public void basicUpdateChangeScores() throws IOException {
        ScheduleUpdater scheduleUpdater = new ScheduleUpdater(teamRepository, gameRepository, aliasRepository, seasonRepository);

        assertThat(gameRepository.findAll()).hasSize(0);
        ObjectMapper mapper = new ObjectMapper();

        InputStream inputStream1 = new ClassPathResource("eg1.json").getInputStream();
        Casablanca value1 = mapper.readValue(inputStream1, Casablanca.class);
        List<UpdateCandidate> updateCandidates1 = value1.extractUpdates();
        InputStream inputStream2 = new ClassPathResource("eg1_new_scores.json").getInputStream();
        Casablanca value2 = mapper.readValue(inputStream2, Casablanca.class);
        List<UpdateCandidate> updateCandidates2 = value2.extractUpdates();

        UpdateResult updateResult1 = scheduleUpdater.updateGamesAndResults("Test", updateCandidates1);
        Iterable<Game> games1 = gameRepository.findAll();
        assertThat(games1).hasSize(15);
        assertThat(games1).allSatisfy(g->assertThat(g.getResult()).isPresent());
        assertThat(updateResult1.getUnmapped()).isEqualTo(0);
        assertThat(updateResult1.getInserts()).isEqualTo(15);
        assertThat(updateResult1.getUpdates()).isEqualTo(0);
        assertThat(updateResult1.getDeletes()).isEqualTo(0);
        assertThat(updateResult1.getUnchanged()).isEqualTo(0);

        UpdateResult updateResult2 = scheduleUpdater.updateGamesAndResults("Test", updateCandidates2);
        Iterable<Game> g2 = gameRepository.findAll();
        assertThat(g2).hasSize(15);
        assertThat(updateResult2.getUnmapped()).isEqualTo(0);
        assertThat(updateResult2.getInserts()).isEqualTo(0);
        assertThat(updateResult2.getUpdates()).isEqualTo(7);
        assertThat(updateResult2.getDeletes()).isEqualTo(0);
        assertThat(updateResult2.getUnchanged()).isEqualTo(8);
        assertThat(g2).allSatisfy(g->assertThat(g.getResult()).isPresent());
    }

}
