package com.fijimf.deepfijomega.repository;

import com.fijimf.deepfijomega.entity.stats.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeriesRepository extends JpaRepository<Series, Long> {
    Optional<Series> findByModelRunIdAndStatisticKey(Long modelRunId, String key);

}
