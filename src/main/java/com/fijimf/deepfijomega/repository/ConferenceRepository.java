package com.fijimf.deepfijomega.repository;

import com.fijimf.deepfijomega.entity.schedule.Conference;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConferenceRepository extends JpaRepository<Conference, Long> {
    @Override
    List<Conference> findAll(Sort sort);
}

