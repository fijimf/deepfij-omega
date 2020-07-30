package com.fijimf.deepfijomega.repository;

import com.fijimf.deepfijomega.entity.schedule.Conference;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConferenceMappingRepository extends CrudRepository<ConferenceMappingRepository, Long> {
}

