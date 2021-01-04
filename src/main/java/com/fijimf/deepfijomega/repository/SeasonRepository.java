package com.fijimf.deepfijomega.repository;

import com.fijimf.deepfijomega.entity.quote.Quote;
import com.fijimf.deepfijomega.entity.schedule.Season;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeasonRepository extends CrudRepository<Season, Long> {
    Optional<Season> findFirstByYear(int year);

    @Override
    List<Season> findAll();

    @Query(nativeQuery = true, value = "select count(*) from game\n" +
            "   inner join season s on s.id = game.season_id\n" +
            "   where s.year=?")
    Integer getSeasonGameCount(int year);

    @Query(nativeQuery = true, value = "select year from season")
    List<Integer> getSeasonYears();
}


