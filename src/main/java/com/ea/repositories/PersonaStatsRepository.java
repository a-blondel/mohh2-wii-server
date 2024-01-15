package com.ea.repositories;

import com.ea.entities.PersonaStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonaStatsRepository extends JpaRepository<PersonaStatsEntity, Long> {

    Optional<PersonaStatsEntity> findByPersonaId(Long id);
    @Query(value = "SELECT RANK FROM " +
            "(SELECT PERSONA_ID, ROW_NUMBER() OVER(ORDER BY (TOTAL_KILLS - TOTAL_DEATHS) DESC, PERSONA_ID ASC) AS RANK FROM PERSONA_STATS)" +
            "AS STATS WHERE STATS.PERSONA_ID = ?1", nativeQuery = true)
    Long getRankById(long id);
}
