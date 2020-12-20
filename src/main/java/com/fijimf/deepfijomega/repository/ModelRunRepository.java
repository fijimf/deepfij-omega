package com.fijimf.deepfijomega.repository;

import com.fijimf.deepfijomega.entity.stats.ModelRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelRunRepository extends JpaRepository<ModelRun, Long> {

}
