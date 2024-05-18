package com.example.api.service;

import com.example.api.domain.Coupon;
import com.example.api.producer.CouponCreateProducer;
import com.example.api.repository.AppliedUserRepository;
import com.example.api.repository.CouponCountRepository;
import com.example.api.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {

  private final CouponRepository couponRepository;
  private final CouponCountRepository couponCountRepository;
  private final AppliedUserRepository appliedUserRepository;
  private final CouponCreateProducer couponCreateProducer;

//  @Transactional
  public Coupon createCouponForRedis(Long userId) {
    long count = couponCountRepository.increment();

    if (count > 100) {
      return null;
    }

    Coupon coupon = Coupon.builder()
      .userId(userId).build();
    return couponRepository.save(coupon);
  }

  public void createCouponForKafka(Long userId) {
    long count = couponCountRepository.increment();

    if (count > 100) {
      return;
    }

    couponCreateProducer.create(userId);
  }

  public void createCouponForKafkaUsingRedisSet(Long userId) {
    Long apply = appliedUserRepository.add(userId);

    // 만약 추가된 개수가 1이 아니라면 해당 유저는 이미 발급 요청을 했던 유저로 판단
    if (apply != 1) {
      log.info("userId = {} 는 이미 쿠폰이 발급된 상태입니다.", userId);
      return;
    }

    long count = couponCountRepository.increment();

    if (count > 100) {
      return;
    }

    couponCreateProducer.create(userId);
  }


}
