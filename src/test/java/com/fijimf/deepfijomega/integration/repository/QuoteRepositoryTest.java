package com.fijimf.deepfijomega.integration.repository;

import com.fijimf.deepfijomega.entity.quote.Quote;
import com.fijimf.deepfijomega.entity.schedule.Team;
import com.fijimf.deepfijomega.integration.utility.DockerPostgresDb;
import com.fijimf.deepfijomega.repository.QuoteRepository;
import com.fijimf.deepfijomega.repository.TeamRepository;
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
public class QuoteRepositoryTest {
    private static final DockerPostgresDb dockerDb = new DockerPostgresDb("postgres:13-alpine", 59432);

    @Autowired
    private QuoteRepository repository;

    @BeforeAll
    public static void spinUpDatabase() throws DockerCertificateException, DockerException, InterruptedException {
        dockerDb.spinUpDatabase();
    }

    @AfterAll
    public static void spinDownDb() throws DockerException, InterruptedException {
        dockerDb.spinDownDb();
    }

    @Test
    public void findAllQuotes() {
        List<Quote> quotes = Lists.newArrayList(repository.findAll());
        int nQuotes = 114;
        assertThat(quotes).hasSize(nQuotes);
    }

    @Test
    public void findRandom() {
        Optional<Quote> q1 = repository.getRandomQuote();
        assertThat(q1).isPresent();
    }

    @Test
    public void findRandomByKeyPresent() {
        Optional<Quote> q1 = repository.getRandomQuote("texas");
        assertThat(q1).isPresent();
    }

    @Test
    public void findRandomByKeyAbsent() {
        Optional<Quote> q2 = repository.getRandomQuote("tag-which-doesnt-exist");
        assertThat(q2).isEmpty();
    }


}