package com.fijimf.deepfijomega.repository;

import com.fijimf.deepfijomega.entity.user.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {

    Optional<Role> findFirstByRoleName(String user);
}

