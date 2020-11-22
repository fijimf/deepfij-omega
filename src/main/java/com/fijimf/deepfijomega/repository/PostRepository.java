package com.fijimf.deepfijomega.repository;

import com.fijimf.deepfijomega.entity.content.Post;
import com.fijimf.deepfijomega.entity.quote.Quote;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends CrudRepository<Post, Long> {

}
