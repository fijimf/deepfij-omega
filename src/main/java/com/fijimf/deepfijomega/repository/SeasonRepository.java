package com.fijimf.deepfijomega.repository;

import com.fijimf.deepfijomega.entity.schedule.Season;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeasonRepository extends CrudRepository<Season, Long> {
   Optional<Season> findFirstByYear(int year);

   @Override
   List<Season> findAll();
}

