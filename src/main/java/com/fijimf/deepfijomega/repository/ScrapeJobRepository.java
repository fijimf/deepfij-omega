package com.fijimf.deepfijomega.repository;

import com.fijimf.deepfijomega.entity.scraping.ScrapeJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScrapeJobRepository extends JpaRepository<ScrapeJob, Long> {

    List<ScrapeJob> findAllBySeason(int season);
}

