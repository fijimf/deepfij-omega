package com.fijimf.deepfijomega.repository;

import com.fijimf.deepfijomega.entity.schedule.Team;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends CrudRepository<Team, Long> {
}

