package com.example.insurance_project.config;

import com.example.insurance_project.kafka.dto.ContractCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka Consumer 관련 설정을 담당하는 클래스입니다.
 * 특히 Kafka로부터 JSON 형태의 메시지를 받아 Java 객체(DTO)로 변환하기 위한 설정을 포함합니다.
 */
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * ContractCreatedEvent 객체를 수신하기 위한 ConsumerFactory를 생성합니다.
     * ConsumerFactory는 Kafka Consumer 인스턴스를 어떻게 만들지 정의합니다.
     * @return ConsumerFactory<String, ContractCreatedEvent> 객체
     */
    @Bean
    public ConsumerFactory<String, ContractCreatedEvent> contractEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        // Kafka 클러스터 주소 설정
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // JSON 메시지를 역직렬화(JSON String -> Java Object)하기 위한 설정
        JsonDeserializer<ContractCreatedEvent> deserializer = new JsonDeserializer<>(ContractCreatedEvent.class);
        // 타입 정보를 헤더에서 제거할지 여부 (false로 설정해야 정상 동작)
        deserializer.setRemoveTypeHeaders(false);
        // 신뢰할 수 있는 패키지 설정. '*'는 모든 패키지를 신뢰한다는 의미입니다.
        // 보안을 위해서는 특정 패키지만 명시하는 것이 좋습니다. (e.g., "com.example.insurance_project.kafka.dto")
        deserializer.addTrustedPackages("*");
        // 키에 대한 타입 매퍼 사용 여부
        

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(), // 키는 String으로 역직렬화
                deserializer // 값은 위에서 설정한 JsonDeserializer를 사용
        );
    }

    /**
     * contractEventConsumerFactory를 기반으로 실제 메시지를 수신하는 리스너 컨테이너를 생성합니다.
     * @KafkaListener 어노테이션이 이 컨테이너 팩토리를 사용하여 메시지를 처리합니다.
     * @return ConcurrentKafkaListenerContainerFactory<String, ContractCreatedEvent> 객체
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ContractCreatedEvent> contractEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ContractCreatedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(contractEventConsumerFactory());
        return factory;
    }
}
