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
import org.springframework.web.client.RestClientException;
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

    public RequestResult scrape(LocalDate date){
        String url=urlFromKey(date);
        LocalDateTime start = LocalDateTime.now();
        String body="";
        int returnCode =-1;
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            returnCode = response.getStatusCodeValue();
            body = response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ce) {
            body = "";
            returnCode=ce.getStatusCode().value();
        }
        LocalDateTime end = LocalDateTime.now();

        String digest = DigestUtils.md5DigestAsHex(body.getBytes());
        try {
            Casablanca c = objectMapper.readValue(body, Casablanca.class);
            List<UpdateCandidate> updateCandidates = c.extractUpdates();
            return new RequestResult(url,returnCode,digest,start, end, updateCandidates);
        } catch (JsonProcessingException e) {
            log.error("Exception processing JSON response", e);
            return new RequestResult(url,returnCode,digest,start, end, List.of());
        }
    }

    public static String urlFromKey(LocalDate date){
       return String.format(
               "https://data.ncaa.com/casablanca/scoreboard/basketball-men/d1/%04d/%02d/%02d/scoreboard.json",
               date.getYear(),
               date.getMonthValue(),
               date.getDayOfMonth()
       );
    }
}