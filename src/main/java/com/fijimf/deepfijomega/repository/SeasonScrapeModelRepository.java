package com.fijimf.deepfijomega.repository;

import com.fijimf.deepfijomega.entity.scraping.SeasonScrapeModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeasonScrapeModelRepository extends CrudRepository<SeasonScrapeModel, Long> {
   public List<SeasonScrapeModel> findByYear(Integer year);
}

