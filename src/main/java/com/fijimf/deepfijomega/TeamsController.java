package com.fijimf.deepfijomega;

import com.fijimf.deepfijomega.scraping.CasablancaScraper;
import com.fijimf.deepfijomega.scraping.Scraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class TeamsController {

    @Autowired
    private Scraper scraper;

    @GetMapping("/doit")
    public String doIt() {
        return CasablancaScraper.scrape(LocalDate.of(2018,2,5));
    }
}
