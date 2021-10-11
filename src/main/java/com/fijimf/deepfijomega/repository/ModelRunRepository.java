package com.fijimf.deepfijomega.repository;

import com.fijimf.deepfijomega.entity.stats.ModelRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModelRunRepository extends JpaRepository<ModelRun, Long> {
   Optional<ModelRun> findByModelKeyAndSeasonId(String modelKey, long id);

   @Query("select model.key from ModelRun where seasonId = ?1")
   List<String> findModelKeyBySeasonId( long id);
}
