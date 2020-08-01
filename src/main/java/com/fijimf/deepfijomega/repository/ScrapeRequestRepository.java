package com.fijimf.deepfijomega.repository;

import com.fijimf.deepfijomega.entity.scraping.ScrapeJob;
import com.fijimf.deepfijomega.entity.scraping.ScrapeRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScrapeRequestRepository extends CrudRepository<ScrapeRequest, Long> {
}

