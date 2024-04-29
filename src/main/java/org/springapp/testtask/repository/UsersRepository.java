package org.springapp.testtask.repository;

import org.springapp.testtask.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface UsersRepository extends JpaRepository<UserEntity, Long> {
    List<UserEntity> getUserEntitiesByDateOfBirthBetween(Date from, Date to);
    Boolean existsByEmail(String email);
}
