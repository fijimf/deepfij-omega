package com.fijimf.deepfijomega.integration;

import com.fijimf.deepfijomega.integration.utility.DockerPostgresDb;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.spotify.docker.client.exceptions.DockerException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(
        properties = {"spring.datasource.url=jdbc:postgresql://localhost:57373/postgres"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureWebTestClient
class DeepfijOmegaApplicationTests {
    private static final DockerPostgresDb dockerDb = new DockerPostgresDb("postgres:13-alpine", 57373);
    public static final String LOCALHOST = "localhost";
    @LocalServerPort
    int port;

    @BeforeAll
    public static void spinUpDatabase() throws DockerException, InterruptedException {
        dockerDb.spinUpDatabase();
    }

    @AfterAll
    public static void spinDownDatabase() throws DockerException, InterruptedException {
        dockerDb.spinDownDb();
    }

    @Test
    void contextLoads() {
        WebTestClient client = WebTestClient.bindToServer().build();
        WebTestClient.ResponseSpec exchange = client.get().uri(getUri(LOCALHOST, port, "/index")).exchange();
        exchange.expectStatus().isOk();
    }

    @NotNull
    private String getUri(final String host, int port, String resource) {
        return "http://" + host + ":" + port + resource;
    }

    @Test
    void testSkeletonPageStructure() throws IOException {
        try (final WebClient webClient = new WebClient()) {
            webClient.getOptions().setJavaScriptEnabled(false);
            for (String request : new String[]{"/", "/index", "/login", "/changePassword", "/forgotPassword", "/signup", "/teams"}) {
                final HtmlPage page = webClient.getPage(getUri(LOCALHOST, port, request));
                assertThat(page)
                        .describedAs("Page for %s is not null", request)
                        .isNotNull()
                        .describedAs("Page for %s satisfies basic structure", request)
                        .satisfies(p -> {
                            assertThat(p.getElementById("header"))
                                    .describedAs("Header for %s is not null", request)
                                    .isNotNull()
                                    .describedAs("Header for %s satisfies header requirements", request)
                                    .satisfies(headerRequirements(request));
                            assertThat(p.getElementById("contentBody"))
                                    .describedAs("ContentBody for %s is not null", request)
                                    .isNotNull()
                                    .describedAs("ContentBody for %s satisfies contentBody requirements", request)
                                    .satisfies(contentBodyRequirements(request));
                            assertThat(p.getElementById("quotebar"))
                                    .describedAs("Quotebar for %s is not null", request)
                                    .isNotNull();
                            assertThat(p.getElementById("sidebar"))
                                    .describedAs("Sidebar for %s is not null", request)
                                    .isNotNull();
                            assertThat(p.getElementById("mainContent"))
                                    .describedAs("MainContent for %s is not null", request)
                                    .isNotNull();
                            assertThat(p.getElementById("footer"))
                                    .describedAs("Footer for %s is not null", request)
                                    .isNotNull();
                        });
            }
        }
    }

    private Consumer<DomElement> headerRequirements(String request) {
        return d -> {
            assertThat(d)
                    .describedAs("Header is not null for %s", request)
                    .isNotNull();
            assertThat(d.getTagName())
                    .describedAs("Header is a div for %s", request)
                    .isEqualTo("div");
            assertThat(d.getAttribute("style"))
                    .describedAs("Header style is empty for %s", request)
                    .isBlank();
            assertThat(d.getAttribute("class"))
                    .describedAs("Header class is correct for %s", request)
                    .isEqualTo("header d-flex flex-row align-items-center justify-content-between p-1");
        };
    }

    private final Consumer<DomElement> contentBodyRequirements(String request) {
        return d -> {
            assertThat(d)
                    .describedAs("ContentBody is a not null for %s", request)
                    .isNotNull();
            assertThat(d.getTagName())
                    .describedAs("ContentBody is a div for %s", request)
                    .isEqualTo("div");
            assertThat(d.getAttribute("style"))
                    .describedAs("ContentBody style is empty for %s", request)
                    .isBlank();
            assertThat(d.getAttribute("class"))
                    .describedAs("ContentBody class is correct for %s", request)
                    .isEqualTo("d-flex flex-row");
            assertThat(d.getChildElementCount())
                    .describedAs("ContentBody has 2 and only 2 for children for %s", request)
                    .isEqualTo(2);
        };
    }

    @Test
    void testImgsHaveAlts() throws IOException {
        try (final WebClient webClient = new WebClient()) {
            webClient.getOptions().setJavaScriptEnabled(false);
            for (String request : new String[]{"/", "/index", "/login", "/changePassword", "/forgotPassword", "/signup", "/teams"}) {
                final HtmlPage page = webClient.getPage(getUri(LOCALHOST, port, request));
                DomNodeList<DomElement> imgs = page.getElementsByTagName("img");
                imgs.forEach(i -> {
                    assertThat(i.getAttribute("alt")).describedAs("Blank image in %s", request).isNotBlank();
                });
            }
        }
    }

    @Test
    void testThsHaveScopes() throws IOException {
        try (final WebClient webClient = new WebClient()) {
            webClient.getOptions().setJavaScriptEnabled(false);
            for (String request : new String[]{"/", "/index", "/login", "/changePassword", "/forgotPassword", "/signup", "/teams"}) {
                final HtmlPage page = webClient.getPage(getUri(LOCALHOST, port, request));
                DomNodeList<DomElement> imgs = page.getElementsByTagName("th");
                imgs.forEach(i -> {
                    assertThat(i.getAttribute("scope")).isNotBlank();
                });
            }
        }
    }


}
