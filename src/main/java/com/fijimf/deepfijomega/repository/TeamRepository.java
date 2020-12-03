package com.fijimf.deepfijomega.repository;

import com.fijimf.deepfijomega.entity.schedule.Team;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findFirstByKey(String key);

    @Override
    List<Team> findAll(Sort sort);
}

