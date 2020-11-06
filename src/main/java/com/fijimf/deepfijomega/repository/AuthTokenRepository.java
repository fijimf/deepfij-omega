package com.fijimf.deepfijomega.repository;

import com.fijimf.deepfijomega.entity.user.AuthToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthTokenRepository extends CrudRepository<AuthToken, Long> {
    Optional<AuthToken> findByToken(String token);
    List<AuthToken> findAllByUserId(long userId);
}

