package com.example.api.service;

import com.example.api.domain.Coupon;
import com.example.api.producer.CouponCreateProducer;
import com.example.api.repository.CouponCountRepository;
import com.example.api.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponService {

  private final CouponRepository couponRepository;
  private final CouponCountRepository couponCountRepository;
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


}
