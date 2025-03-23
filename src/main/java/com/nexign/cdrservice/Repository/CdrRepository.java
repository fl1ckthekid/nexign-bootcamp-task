package com.nexign.cdrservice.Repository;

import com.nexign.cdrservice.Entity.CdrEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CdrRepository extends JpaRepository<CdrEntity, Long> {
    List<CdrEntity> findByCallerOrReceiverAndStartTimeBetween(String caller, String receiver, LocalDateTime start, LocalDateTime end);

    List<CdrEntity> findByCallerOrReceiver(String caller, String receiver);
}
