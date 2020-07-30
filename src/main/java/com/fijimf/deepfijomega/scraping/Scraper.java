package com.fijimf.deepfijomega.scraping;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class Scraper {

    public String scrape() {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = "http://fijimf.com/";
        ResponseEntity<String> response
                = restTemplate.getForEntity(fooResourceUrl, String.class);
        return response.getBody();
    }
}

