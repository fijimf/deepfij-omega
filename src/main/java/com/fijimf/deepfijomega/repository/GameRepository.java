package com.fijimf.deepfijomega.repository;

import com.fijimf.deepfijomega.entity.schedule.Game;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends CrudRepository<Game, Long> {
    List<Game> findAllByLoadKey(String key);
}


