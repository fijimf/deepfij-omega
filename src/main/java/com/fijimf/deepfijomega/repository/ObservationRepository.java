package com.fijimf.deepfijomega.repository;

import com.fijimf.deepfijomega.entity.stats.Observation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObservationRepository extends JpaRepository<Observation, Long> {
  
}
