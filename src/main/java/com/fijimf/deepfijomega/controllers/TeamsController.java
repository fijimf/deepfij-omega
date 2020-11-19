package com.fijimf.deepfijomega.controllers;

import com.fijimf.deepfijomega.scraping.CasablancaScraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TeamsController {

    @Autowired
    private CasablancaScraper scraper;


}
