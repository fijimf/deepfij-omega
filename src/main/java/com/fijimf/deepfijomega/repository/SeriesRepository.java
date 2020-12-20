package com.fijimf.deepfijomega.repository;

import com.fijimf.deepfijomega.entity.stats.Observation;
import com.fijimf.deepfijomega.entity.stats.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeriesRepository extends JpaRepository<Series, Long> {

}
