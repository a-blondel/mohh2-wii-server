package com.ea.repositories;

import com.ea.entities.LobbyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface LobbyRepository extends JpaRepository<LobbyEntity, Long> {

    Optional<LobbyEntity> findById(Long id);

    List<LobbyEntity> findByEndTime(Timestamp endTime);

}
