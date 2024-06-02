package com.example.consumer.scheduler;

import com.example.consumer.domain.FailedEvent;
import com.example.consumer.domain.code.EventStatus;
import com.example.consumer.repository.FailedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponRecreatedScheduler {

  private final FailedEventRepository failedEventRepository;
  private final SchedulerService schedulerService;

  @Scheduled(cron = "0 0/1 * * * ?") // 매 1분마다 실행
  public void recreatedCouponScheduler() {
    log.info("Recreated coupon scheduler Start");
    List<FailedEvent> events = failedEventRepository.findTop50ByEventStatus(
      EventStatus.REGISTERED);

    if (!CollectionUtils.isEmpty(events)) {
      schedulerService.recreateCoupon(events);
    }
    log.info("Recreated coupon scheduler End");
  }

}
