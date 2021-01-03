package com.fijimf.deepfijomega.repository;

import com.fijimf.deepfijomega.entity.stats.Model;
import com.fijimf.deepfijomega.entity.stats.ModelRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {
    Optional<Model> findByKey(String key);
}
