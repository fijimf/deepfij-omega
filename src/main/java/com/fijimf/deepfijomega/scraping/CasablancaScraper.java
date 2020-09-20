package com.fijimf.deepfijomega.scraping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class CasablancaScraper {
    private final static Logger log = LoggerFactory.getLogger(CasablancaScraper.class);
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private final RestTemplate restTemplate = new RestTemplate();

    public RequestResult scrape(LocalDate date) {
        String url = urlFromKey(date);
        LocalDateTime start = LocalDateTime.now();

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            LocalDateTime end = LocalDateTime.now();
            int status = response.getStatusCodeValue();
            String body = response.getBody();
            String digest = DigestUtils.md5DigestAsHex(body != null ? body.getBytes() : new byte[0]);
            if (body == null) {
                return new RequestResult(url, status, digest, start, end, List.of());
            } else {
                Casablanca c = objectMapper.readValue(body, Casablanca.class);
                List<UpdateCandidate> updateCandidates = c.extractUpdates();
                return new RequestResult(url, status, digest, start, end, updateCandidates);
            }
        } catch (JsonProcessingException e) {
            log.error("Exception processing JSON response", e);
            return new RequestResult(url, -1, "", start, null, List.of());
        } catch (HttpClientErrorException | HttpServerErrorException ce) {
            log.warn("Http error " + ce.getMessage());
            return new RequestResult(url, ce.getStatusCode().value(), "", start, null, List.of());
        }
    }

    public static String urlFromKey(LocalDate date) {
        return String.format(
                "https://data.ncaa.com/casablanca/scoreboard/basketball-men/d1/%04d/%02d/%02d/scoreboard.json",
                date.getYear(),
                date.getMonthValue(),
                date.getDayOfMonth()
        );
    }
}