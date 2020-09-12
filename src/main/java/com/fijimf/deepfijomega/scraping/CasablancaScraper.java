package com.fijimf.deepfijomega.scraping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.buf.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CasablancaScraper {
    private final static Logger log = LoggerFactory.getLogger(CasablancaScraper.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    public List<UpdateCandidate> scrape(LocalDate date, Integer jobId){
        return null;
    }
    public List<UpdateCandidate> scrape(LocalDate date){

        String url=urlFromKey(date);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        try {
            Casablanca c = objectMapper.readValue(response.getBody(), Casablanca.class);
            return c.extractUpdates();
        } catch (JsonProcessingException e) {
            log.error("Exception processing JSON response", e);
            return List.of();
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