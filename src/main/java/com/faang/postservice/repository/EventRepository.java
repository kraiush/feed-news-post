package com.faang.postservice.repository;

import com.faang.postservice.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM event WHERE id = :id", nativeQuery = true)
    void deleteById(@Param(value = "id") UUID id);
}


