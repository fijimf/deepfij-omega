package com.fijimf.deepfijomega.integration.repository;

import com.fijimf.deepfijomega.entity.schedule.Season;
import com.fijimf.deepfijomega.entity.schedule.Team;
import com.fijimf.deepfijomega.integration.utility.DockerPostgresDb;
import com.fijimf.deepfijomega.repository.SeasonRepository;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.PortBinding;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.sql.DriverManager;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@EntityScan(basePackages = {"com.fijimf.deepfijomega.entity"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class SeasonRepositoryTest {
    private static final DockerPostgresDb dockerDb = new DockerPostgresDb("postgres:13-alpine",59432);

    @Autowired
    private SeasonRepository repository;

    @BeforeAll
    public static void spinUpDatabase() throws DockerCertificateException, DockerException, InterruptedException {
       dockerDb.spinUpDatabase();
    }

    @AfterAll
    public static void spinDownDb() throws DockerException, InterruptedException {
       dockerDb.spinDownDb();
    }

    @Test
    public void findAllSeasons() {
        List<Season> seasons = Lists.newArrayList(repository.findAll());
        assertEquals(seasons.size(), 7);
    }


}