package com.fijimf.deepfijomega.repository;

import com.fijimf.deepfijomega.entity.stats.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {
    Optional<Model> findByKey(String key);

    @Query("select distinct key from Model")
    List<String> findAllModelKeys();
}
