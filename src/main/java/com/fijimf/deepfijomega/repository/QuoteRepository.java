package com.fijimf.deepfijomega.repository;

import com.fijimf.deepfijomega.entity.quote.Quote;
import com.fijimf.deepfijomega.entity.schedule.Team;
import org.hibernate.annotations.Parameter;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuoteRepository extends CrudRepository<Quote, Long> {
    @Query(nativeQuery = true, value = "select * from quote where tag = :tag order by random() limit 1")
    Optional<Quote> getRandomQuote(@Param("tag") String tag);

    @Query(nativeQuery = true, value = "select * from quote order by random() limit 1")
    Optional<Quote> getRandomQuote();
}

