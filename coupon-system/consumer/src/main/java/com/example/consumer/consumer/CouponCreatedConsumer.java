package com.example.consumer.consumer;

import com.example.consumer.domain.Coupon;
import com.example.consumer.domain.FailedEvent;
import com.example.consumer.repository.CouponRepository;
import com.example.consumer.repository.FailedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponCreatedConsumer {

  private final CouponRepository couponRepository;
  private final FailedEventRepository failedEventRepository;


  @KafkaListener(topics = "coupon_create", groupId = "group_1")
  public void listener(Long userId) {
    log.info("createDateTime = {}, userId = {}", LocalDateTime.now(), userId);

    try {
      if (userId % 2 == 0) {
        couponRepository.save(Coupon.builder()
          .userId(userId).build());
      } else {
        throw new RuntimeException();
      }
    } catch (Exception e) {
      log.error("failed to create coupon, userId = {}", userId);
      // 쿠폰 발행과 발행 신청이 디커플링 되어 있기 때문에 쿠폰 발급 실패 시
      // FailedEvent 에 저장하여 장애에 대응할 수 있도록 함
      failedEventRepository.save(FailedEvent.builder()
        .userId(userId).build());
    }
  }

}
