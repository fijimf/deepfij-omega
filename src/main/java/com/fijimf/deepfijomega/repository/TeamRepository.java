package com.fijimf.deepfijomega.repository;

import com.fijimf.deepfijomega.entity.schedule.Team;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends CrudRepository<Team, Long> {
    Optional<Team> findFirstByKey(String key);
}

