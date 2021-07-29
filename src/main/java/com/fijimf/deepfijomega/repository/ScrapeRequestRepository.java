package com.fijimf.deepfijomega.repository;

import com.fijimf.deepfijomega.entity.scraping.ScrapeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScrapeRequestRepository extends JpaRepository<ScrapeRequest, Long> {
    @Query(value = "select id, job_id, model_key, requested_at, status_code, digest, updates_proposed, updates_accepted\n" +
            "from (\n" +
            "         select sr.*, rank() OVER (PARTITION BY model_key ORDER BY requested_at DESC)\n" +
            "         from scrape_request sr inner join scrape_job sj on sr.job_id = sj.id\n" +
            "         where status_code = 200 and sj.season = ?1) recent_reqs\n" +
            "where rank = 1", nativeQuery = true)
    List<ScrapeRequest> findLatestBySeason(int season);

    List<ScrapeRequest> findScrapeRequestByModelKey(String key);
}

