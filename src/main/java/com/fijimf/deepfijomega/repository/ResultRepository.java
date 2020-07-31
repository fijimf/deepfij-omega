package com.fijimf.deepfijomega.repository;

import com.fijimf.deepfijomega.entity.schedule.Result;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResultRepository extends CrudRepository<Result, Long> {
}


