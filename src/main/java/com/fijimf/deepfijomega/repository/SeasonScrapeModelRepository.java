package com.fijimf.deepfijomega.repository;

import com.fijimf.deepfijomega.entity.scraping.SeasonScrapeModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeasonScrapeModelRepository extends CrudRepository<SeasonScrapeModel, Long> {
   Optional<SeasonScrapeModel> findFirstByYear(Integer year);
}

