package com.ea.repositories;

import com.ea.entities.PersonaConnectionEntity;
import com.ea.entities.PersonaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonaConnectionRepository extends JpaRepository<PersonaConnectionEntity, Long> {

    @Query(value = "SELECT c FROM PersonaConnectionEntity c WHERE c.persona = :persona AND endTime IS NULL")
    Optional<PersonaConnectionEntity> findCurrentPersonaConnection(PersonaEntity persona);

}
