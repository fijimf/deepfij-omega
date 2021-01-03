package com.fijimf.deepfijomega.repository;

import com.fijimf.deepfijomega.entity.stats.Statistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatisticRepository extends JpaRepository<Statistic, Long> {

    Optional<Statistic> findByKey(String key);
}
