package com.fijimf.deepfijomega;

import com.fijimf.deepfijomega.entity.scraping.ScrapeJob;
import com.fijimf.deepfijomega.repository.ScrapeJobRepository;
import com.fijimf.deepfijomega.repository.ScrapeRequestRepository;
import com.fijimf.deepfijomega.scraping.CasablancaScraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Controller
public class ScrapingController {

    @Autowired
    private ScrapeJobRepository jobRepo;

    @Autowired
    private ScrapeRequestRepository requestRepo;

    @GetMapping("/scrape/jobs")
    public String showJobs(Model model) {
        model.addAttribute(jobRepo.findAll());
        return "scrapeJobs";
    }
   @GetMapping("/scrape/fill/{season}")
    public String fill(Model model, Integer season) {
        model.addAttribute(jobRepo.findAll());
        return "scrapeJobs";
    }

   @GetMapping("/scrape/update/{yyyymmdd}")
    public String update(Model model, Integer season) {
        model.addAttribute(jobRepo.findAll());
        return "scrapeJobs";
    }


}
