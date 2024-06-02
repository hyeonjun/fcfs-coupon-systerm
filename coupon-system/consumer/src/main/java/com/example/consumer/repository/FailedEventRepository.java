package com.example.consumer.repository;

import com.example.consumer.domain.FailedEvent;
import com.example.consumer.domain.code.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface FailedEventRepository extends JpaRepository<FailedEvent, Long> {

  @Query("SELECT failEvent FROM FailedEvent failEvent" +
    " WHERE failEvent.eventStatus = :eventStatus" +
    " AND failEvent.retry < 3" +
    " ORDER BY failEvent.id asc" +
    " LIMIT 50")
  List<FailedEvent> findTop50ByEventStatus(EventStatus eventStatus);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("UPDATE FailedEvent failEvent" +
    " SET failEvent.eventStatus = :eventStatus" +
    " WHERE failEvent IN :failedEvents")
  void updateEventStatusByIdIn(Collection<FailedEvent> failedEvents, EventStatus eventStatus);
}
