package com.ea.repositories;

import com.ea.entities.PersonaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<PersonaEntity, Long> {
    Optional<PersonaEntity> findByPers(String name);

    @Query(value = "SELECT RANK FROM " +
            "(SELECT ID, ROW_NUMBER() OVER(ORDER BY (KILLS - DEATHS) DESC, ID ASC) AS RANK FROM PERSONA)" +
            "AS PERS WHERE PERS.ID = ?1", nativeQuery = true)
    Long getPersonaRank(long id);
}
