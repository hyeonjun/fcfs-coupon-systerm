package com.example.consumer.domain;

import com.example.consumer.domain.code.EventStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@NoArgsConstructor
@Getter
public class FailedEvent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Enumerated(EnumType.STRING)
  @Column(name = "event_status", nullable = false)
  @Setter
  private EventStatus eventStatus;

  @Setter
  private int retry;

  @Builder
  protected FailedEvent(Long userId) {
    this.userId = userId;
    this.eventStatus = EventStatus.REGISTERED;
    this.retry = 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    FailedEvent that = (FailedEvent) o;
    return Objects.equals(getId(), that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId());
  }

  @Override
  public String toString() {
    return "FailedEvent{" +
      "id=" + id +
      ", userId=" + userId +
      '}';
  }
}
