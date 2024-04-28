package com.example.api.service;

import com.example.api.domain.Coupon;
import com.example.api.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponService {

  private final CouponRepository couponRepository;

  @Transactional
  public Optional<Coupon> createCoupon(Long userId) {
    long count = couponRepository.count();

    if (count > 100) {
      return Optional.empty();
    }

    Coupon coupon = Coupon.builder()
      .userId(userId).build();
    return Optional.of(couponRepository.save(coupon));
  }

}
