package com.example.api.service;

import com.example.api.domain.Coupon;
import com.example.api.repository.CouponCountRepository;
import com.example.api.repository.CouponRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.example.api.service.TestType.KAFKA_WITH_REDIS_SET;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Rollback(value = false)
class CouponServiceTest {

  @Autowired
  private CouponService couponService;
  @Autowired
  private CouponRepository couponRepository;
  @Autowired
  private CouponCountRepository couponCountRepository;

  private final Logger log = LoggerFactory.getLogger(CouponServiceTest.class);

  @AfterEach
  void after() {
    couponRepository.deleteAll();
    couponCountRepository.flushAll();
  }

  @Test
  void 한명만_응모() {
    Coupon coupon = couponService.createCouponForRedis(1L);

    log.info("coupon: {}", coupon);

    long count = couponRepository.count();

    assertEquals(1L, count);
  }

  @ParameterizedTest
  @EnumSource(value = TestType.class, names = {"REDIS", "KAFKA",})
  void 동시에_1000개의_요청(TestType type) throws InterruptedException {
    int threadCount = 1000;
    // ExecutorService: 병령 작업을 간단하게 할 수 있게 도와주는 Java API
    ExecutorService executorService = Executors.newFixedThreadPool(32);
    // 다른 스레드에서 수행하는 작업을 기다리도록 도와주는 클래스
    CountDownLatch latch = new CountDownLatch(threadCount);

    for (int i=0; i<threadCount; i++) {
      long userId = i;
      executorService.submit(() -> {
        try {
          execute(userId, type);
        } catch (Exception e) {
          log.error(e.getMessage());
        } finally {
          latch.countDown();;
        }
      });
    }

    latch.await();

    // kafka consumer 가 데이터를 받아 실제 db에 저장하기까지 시간이 걸리기 때문에
    // 넉넉하게 10초 정도 기다린 후 개수를 확인한다.
    Thread.sleep(10000L);

    long count = couponRepository.count();

    assertEquals(100L, count);
  }

  @Test
  void 하나의_유저가_1000번_요청() throws InterruptedException {
    int threadCount = 1000;
    // ExecutorService: 병령 작업을 간단하게 할 수 있게 도와주는 Java API
    ExecutorService executorService = Executors.newFixedThreadPool(32);
    // 다른 스레드에서 수행하는 작업을 기다리도록 도와주는 클래스
    CountDownLatch latch = new CountDownLatch(threadCount);

    Long userId = 1L;
    for (int i=0; i<threadCount; i++) {
      executorService.submit(() -> {
        try {
          execute(userId, KAFKA_WITH_REDIS_SET);
        } catch (Exception e) {
          log.error(e.getMessage());
        } finally {
          latch.countDown();;
        }
      });
    }

    latch.await();

    // kafka consumer 가 데이터를 받아 실제 db에 저장하기까지 시간이 걸리기 때문에
    // 넉넉하게 10초 정도 기다린 후 개수를 확인한다.
    Thread.sleep(10000L);

    long count = couponRepository.count();

    assertEquals(1L, count);
  }

  void execute(long userId, TestType type) {
    switch (type) {
      case REDIS:
        couponService.createCouponForRedis(userId);
        break;
      case KAFKA:
        couponService.createCouponForKafka(userId);
        break;
      case KAFKA_WITH_REDIS_SET:
        couponService.createCouponForKafkaUsingRedisSet(userId);
        break;
      default:
        throw new RuntimeException("not supported type");
    }
  }
}

enum TestType {
  REDIS,
  KAFKA,
  KAFKA_WITH_REDIS_SET
}
