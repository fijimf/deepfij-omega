package com.fijimf.deepfijomega.integration.repository;

import com.fijimf.deepfijomega.entity.schedule.Alias;
import com.fijimf.deepfijomega.integration.utility.DockerPostgresDb;
import com.fijimf.deepfijomega.repository.AliasRepository;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EntityScan(basePackages = {"com.fijimf.deepfijomega.entity"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class AliasRepositoryTest {
    private static final DockerPostgresDb dockerDb = new DockerPostgresDb("postgres:13-alpine",59432);

    @Autowired
    private AliasRepository repository;

    @BeforeAll
    public static void spinUpDatabase() throws DockerCertificateException, DockerException, InterruptedException {
        dockerDb.spinUpDatabase();
    }

    @AfterAll
    public static void spinDownDb() throws DockerException, InterruptedException {
        dockerDb.spinDownDb();
    }

    @Test
    public void findAllTeams() {
        List<Alias> aliases = Lists.newArrayList(repository.findAll());
        int nAliases = 161;
        assertThat(aliases).hasSize(nAliases);
        assertThat(aliases).allMatch(alias -> alias.getTeam()!=null);
    }

    @Test
    public void findFirstByAlias() {
        Optional<Alias> loyola = repository.findFirstByValue("loyola-chicago");
        assertThat(loyola).isPresent();

        Optional<Alias> fakeAlias = repository.findFirstByValue("not-here-chicago");
        assertThat(fakeAlias).isEmpty();
    }

}