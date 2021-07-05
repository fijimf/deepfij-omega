package com.fijimf.deepfijomega.repository;

import com.fijimf.deepfijomega.entity.scraping.SeasonScrapeModel;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeasonScrapeModelRepository extends JpaRepository<SeasonScrapeModel, Long> {
   Optional<SeasonScrapeModel> findFirstByYear(Integer year);

   @Override
   List<SeasonScrapeModel> findAll(Sort sort);
}

