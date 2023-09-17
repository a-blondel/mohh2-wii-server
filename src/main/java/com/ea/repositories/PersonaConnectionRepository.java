package com.ea.repositories;

import com.ea.entities.PersonaConnectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonaConnectionRepository extends JpaRepository<PersonaConnectionEntity, Long> {
}
