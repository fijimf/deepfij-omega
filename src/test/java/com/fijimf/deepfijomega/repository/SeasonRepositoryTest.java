package com.fijimf.deepfijomega.repository;

import com.fijimf.deepfijomega.entity.schedule.Season;
import com.fijimf.deepfijomega.entity.schedule.Team;
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

import java.sql.DriverManager;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@EntityScan(basePackages = {"com.fijimf.deepfijomega.entity"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SeasonRepositoryTest {
    private static String containerId;
    private static DockerClient docker;

    @Autowired
    private SeasonRepository repository;

    @BeforeAll
    public static void spinUpDatabase() throws DockerCertificateException, DockerException, InterruptedException {
        docker = DefaultDockerClient.fromEnv().build();
        String containerName = UUID.randomUUID().toString();
        String port = "55432";
        List<PortBinding> hostPorts = new ArrayList<>();
        hostPorts.add(PortBinding.of("0.0.0.0", port));
        Map<String, List<PortBinding>> portBindings = new HashMap<>();
        portBindings.put("5432/tcp", hostPorts);

        HostConfig hostConfig = HostConfig.builder().portBindings(portBindings).build();


        ContainerConfig containerConfig = ContainerConfig
                .builder()
                .hostConfig(hostConfig)
                .image("postgres:13-alpine")
                .exposedPorts(port + "/tcp")
                .env("POSTGRES_USER=postgres", "POSTGRES_PASSWORD=p@ssw0rd")
                .build();
        ContainerCreation creation = docker.createContainer(containerConfig, containerName);
        containerId= creation.id();
        docker.startContainer(containerId);
        readyCheck(20,500L);
    }

    private static void readyCheck(int tries, long backoffMillis) {
        if (tries <= 0) throw new RuntimeException("Timed out testing db connection");
        try {
            Class.forName("org.postgresql.Driver");
            DriverManager.getConnection("jdbc:postgresql://localhost:55432/postgres", "postgres", "p@ssw0rd");
        } catch (Exception e) {
            try {
                Thread.sleep(backoffMillis);
            } catch (InterruptedException ignored) {
            }
            readyCheck(tries - 1, backoffMillis);
        }
    }

    @AfterAll
    public static void spinDownDb() throws DockerException, InterruptedException {
        docker.stopContainer(containerId,2);
        docker.removeContainer(containerId);
    }

    @Test
    public void findAllTeams() {
        List<Season> seasons = Lists.newArrayList(repository.findAll());
        assertEquals(seasons.size(), 7);
    }


}