package com.fijimf.deepfijomega.repository;

import com.fijimf.deepfijomega.entity.schedule.Alias;
import com.fijimf.deepfijomega.entity.schedule.Team;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AliasRepository extends CrudRepository<Alias, Long> {
}

