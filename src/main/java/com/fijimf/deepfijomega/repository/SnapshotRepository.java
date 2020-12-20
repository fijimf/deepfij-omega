package com.fijimf.deepfijomega.repository;

import com.fijimf.deepfijomega.entity.stats.Series;
import com.fijimf.deepfijomega.entity.stats.Snapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SnapshotRepository extends JpaRepository<Snapshot, Long> {

}
