package com.fijimf.deepfijomega.repository;

import com.fijimf.deepfijomega.entity.scraping.ScrapeJob;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScrapeJobRepository extends CrudRepository<ScrapeJob, Long> {
}

