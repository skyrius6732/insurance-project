package com.example.insurance_project.config;

import com.example.insurance_project.kafka.dto.ContractCreatedEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka Producer 관련 설정을 담당하는 클래스입니다.
 * 특히 Java 객체(DTO)를 JSON 형태로 Kafka에 전송하기 위한 설정을 포함합니다.
 */
@Configuration
public class KafkaProducerConfig {

    // application.properties에 정의된 카프카 서버 주소를 주입받습니다.
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * ContractCreatedEvent 객체를 전송하기 위한 ProducerFactory를 생성합니다.
     * ProducerFactory는 Kafka Producer 인스턴스를 어떻게 만들지 정의합니다.
     * @return ProducerFactory<String, ContractCreatedEvent> 객체
     */
    @Bean
    public ProducerFactory<String, ContractCreatedEvent> contractEventProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        // Kafka 클러스터 주소 설정
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // 메시지의 키를 직렬화할 때 사용할 클래스 지정 (String)
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // 메시지의 값(value)을 직렬화할 때 사용할 클래스 지정 (JSON)
        // ContractCreatedEvent 객체를 JSON 문자열로 변환해줍니다.
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * contractEventProducerFactory를 기반으로 실제 메시지를 보내는 데 사용할 KafkaTemplate을 생성합니다.
     * KafkaTemplate은 메시지 전송을 위한 편리한 메서드들을 제공하는 래퍼 클래스입니다.
     * @return KafkaTemplate<String, ContractCreatedEvent> 객체
     */
    @Bean
    public KafkaTemplate<String, ContractCreatedEvent> contractEventKafkaTemplate() {
        return new KafkaTemplate<>(contractEventProducerFactory());
    }

    /**
     * 일반 문자열 메시지를 전송하기 위한 ProducerFactory를 생성합니다.
     * @return ProducerFactory<String, String> 객체
     */
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * producerFactory를 기반으로 일반 문자열 메시지를 보내는 데 사용할 KafkaTemplate을 생성합니다.
     * @return KafkaTemplate<String, String> 객체
     */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
