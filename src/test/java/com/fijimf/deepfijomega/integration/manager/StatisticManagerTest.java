package com.fijimf.deepfijomega.integration.manager;

import com.fijimf.deepfijomega.entity.schedule.Season;
import com.fijimf.deepfijomega.entity.stats.Model;
import com.fijimf.deepfijomega.entity.stats.ModelRun;
import com.fijimf.deepfijomega.entity.stats.Series;
import com.fijimf.deepfijomega.entity.stats.Statistic;
import com.fijimf.deepfijomega.integration.utility.DockerPostgresDb;
import com.fijimf.deepfijomega.manager.StatisticManager;
import com.fijimf.deepfijomega.repository.*;
import com.spotify.docker.client.exceptions.DockerException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EntityScan(basePackages = {"com.fijimf.deepfijomega.entity"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class StatisticManagerTest {
    private static final DockerPostgresDb dockerDb = new DockerPostgresDb("postgres:13-alpine", 59432);

    @Autowired
    ModelRunRepository modelRunRepo;

    @Autowired
    ModelRepository modelRepo;

    @Autowired
    SeasonRepository seasonRepo;

    @Autowired
    StatisticRepository statRepo;

    @Autowired
    SeriesRepository seriesRepo;

    @Autowired
    SnapshotRepository snapRepo;

    @Autowired
    ObservationRepository obsRepo;

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
        StatisticManager mgr = new StatisticManager(seasonRepo, modelRunRepo, modelRepo, seriesRepo, snapRepo, obsRepo, statRepo);
        assertThat(mgr).isNotNull();
    }

    @Test
    public void find() {
        Model model = modelRepo.save(new Model("key", "Name"));
        Season season = seasonRepo.save(new Season(1999));
        ModelRun modelRun = modelRunRepo.save(new ModelRun(model, season.getId(), LocalDateTime.now()));

        Optional<ModelRun> omr = modelRunRepo.findByModelKeyAndSeasonId("key", season.getId());

        assertThat(omr)
                .isNotEmpty()
                .hasValueSatisfying(mr -> assertThat(mr).isEqualToComparingFieldByField(modelRun));
    }

    @Test
    public void saveModelRun() {
        Model model = modelRepo.save(new Model("key", "Name"));
        Season season = seasonRepo.save(new Season(1999));
        ModelRun modelRun = modelRunRepo.save(new ModelRun(model, season.getId(), LocalDateTime.now()));
        List<ModelRun> modelRuns = modelRunRepo.findAll();
        assertThat(modelRuns).hasSize(1);
    }

    @Test
    public void saveSeries() {
        Model model = modelRepo.save(new Model("key", "Name"));
        Season season = seasonRepo.save(new Season(1999));
        ModelRun modelRun = modelRunRepo.save(new ModelRun(model, season.getId(), LocalDateTime.now()));

        Statistic stat = statRepo.save(new Statistic("xxx","XXX", model.getId(), true, null, "%0.7f"));
        Series ser1 = seriesRepo.save(new Series(modelRun,stat,List.of()));
        Series ser = ser1;
        assertThat(ser.getId()).isGreaterThan(0L);

    }


}
