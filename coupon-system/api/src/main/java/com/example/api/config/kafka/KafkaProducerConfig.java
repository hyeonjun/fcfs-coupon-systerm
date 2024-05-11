package com.example.api.config.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

  // Producer 인스턴스를 생성하는 데 필요한 설정 값들을 세팅
  @Bean
  public ProducerFactory<String, Long> producerFactory() {
    Map<String, Object> config = new HashMap<>();

    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, LongSerializer.class);

    return new DefaultKafkaProducerFactory<>(config);
  }

  // 카프카 토픽에 데이터를 전송하기 위해 사용할 카프카 템플릿을 생성
  // 카프카 템플릿을 빈으로 등록하기 위한 메소드를 생성해주고
  // 카프카 템플릿을 생성할 때 위의 producerFactory 를 전달
  @Bean
  public KafkaTemplate<String, Long> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }

}
