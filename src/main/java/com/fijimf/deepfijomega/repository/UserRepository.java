package com.fijimf.deepfijomega.repository;

import com.fijimf.deepfijomega.entity.schedule.Team;
import com.fijimf.deepfijomega.entity.user.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findFirstByUsername(String username);
    Optional<User> findFirstByEmail(String username);
}

