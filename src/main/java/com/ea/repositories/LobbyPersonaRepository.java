package com.ea.repositories;

import com.ea.entities.LobbyReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LobbyPersonaRepository extends JpaRepository<LobbyReportEntity, Long> {

}
