package com.fijimf.deepfijomega.repository;

import com.fijimf.deepfijomega.entity.scraping.SeasonScrapeModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeasonScrapeModelRepository extends CrudRepository<SeasonScrapeModel, Long> {
}

