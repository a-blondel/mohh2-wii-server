package com.ea.repositories;

import com.ea.entities.LobbyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface LobbyRepository extends JpaRepository<LobbyEntity, Long> {

    List<LobbyEntity> findByEndTime(Timestamp endTime);

}
