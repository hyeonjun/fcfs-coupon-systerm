package com.example.consumer.scheduler;

import com.example.consumer.domain.Coupon;
import com.example.consumer.domain.FailedEvent;
import com.example.consumer.domain.code.EventStatus;
import com.example.consumer.repository.CouponRepository;
import com.example.consumer.repository.FailedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {

  private final FailedEventRepository failedEventRepository;
  private final CouponRepository couponRepository;

  private final int FAILED_COUNT_LIMIT = 2;

  @Transactional
  public void recreateCoupon(List<FailedEvent> events) {
    failedEventRepository.updateEventStatusByIdIn(events, EventStatus.RUNNING);
    List<FailedEvent> updateEvents = new ArrayList<>();
    for (FailedEvent failedEvent : events) {
      try {
        couponRepository.save(Coupon.builder()
          .userId(failedEvent.getUserId()).build());

        updateFailedEventStatus(failedEvent, EventStatus.SUCCESS);
      } catch (Exception e) {
        log.error(e.getMessage());
        updateFailedEventStatus(failedEvent, EventStatus.FAIL);
      }
      updateEvents.add(failedEvent);
    }
    failedEventRepository.saveAll(updateEvents);
  }

  private void updateFailedEventStatus(FailedEvent failedEvent, EventStatus eventStatus) {
    if (EventStatus.SUCCESS.equals(eventStatus)) {
      failedEvent.setEventStatus(eventStatus);
    } else { // FAIL
      if (failedEvent.getRetry() < FAILED_COUNT_LIMIT) {
        failedEvent.setEventStatus(EventStatus.REGISTERED);
      } else {
        failedEvent.setEventStatus(eventStatus);
      }
    }
    failedEvent.setRetry(failedEvent.getRetry() + 1);
  }

}
