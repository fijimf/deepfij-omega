package com.fijimf.deepfijomega.repository;

import com.fijimf.deepfijomega.entity.schedule.Season;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeasonRepository extends CrudRepository<Season, Long> {
}

