package com.fijimf.deepfijomega.repository;

import com.fijimf.deepfijomega.entity.scraping.ScrapeJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScrapeJobRepository extends JpaRepository<ScrapeJob, Long> {

    List<ScrapeJob> findAllBySeason(int season);

    @Query("SELECT s FROM ScrapeJob s where s.season = ?1 AND s.model = ?2")
    List<ScrapeJob> findAllBySeasonAndModelName(int season, String modelName);
}

