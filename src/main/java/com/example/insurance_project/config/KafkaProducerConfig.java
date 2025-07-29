package com.example.insurance_project.config;

// import com.example.insurance_project.kafka.dto.ContractCreatedEvent; // 제거
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

    // application.properties에 정의된 카프카 서버 주소를 주입받습니다。
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    // contractEventProducerFactory() 메서드 제거
    // contractEventKafkaTemplate() 메서드 제거

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
