package com.example.consumer.consumer;

import com.example.consumer.domain.Coupon;
import com.example.consumer.repository.CouponRepository;
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

  @KafkaListener(topics = "coupon_create", groupId = "group_1")
  public void listener(Long userId) {
    log.info("createDateTime = {}, userId = {}", LocalDateTime.now(), userId);

    couponRepository.save(Coupon.builder()
      .userId(userId).build());
  }

}
