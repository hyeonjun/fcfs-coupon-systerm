package com.example.api.controller;


import com.example.api.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
public class CouponController {

  private final CouponService couponService;

  @PostMapping("/{id}")
  public ResponseEntity<Void> createCoupon(@PathVariable Long id) {
    couponService.createCouponForKafkaUsingRedisSet(id);
    return ResponseEntity.ok().build();
  }
}
