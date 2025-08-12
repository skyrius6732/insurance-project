package com.example.insurance_project.config;

import com.example.insurance_project.kafka.avro.InsuranceEvent; // Avro InsuranceEvent 임포트
import io.confluent.kafka.serializers.KafkaAvroSerializer; // KafkaAvroSerializer 임포트
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
// import org.springframework.kafka.support.serializer.JsonSerializer; // JSON serializer는 더 이상 사용하지 않으므로 주석 처리

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Kafka Producer 관련 설정을 담당하는 클래스입니다.
 * 특히 Java 객체(DTO)를 JSON 형태로 Kafka에 전송하기 위한 설정을 포함합니다.
 */
@Configuration
public class KafkaProducerConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerConfig.class);

    /**
     * InsuranceEvent 객체를 Avro 형식으로 직렬화하여 전송하기 위한 ProducerFactory를 생성합니다.
     * @return ProducerFactory<String, InsuranceEvent> 객체
     */
    @Bean
    public ProducerFactory<String, InsuranceEvent> insuranceEventProducerFactory(KafkaProperties kafkaProperties, SslBundles sslBundles) {
        log.info("DEBUG: insuranceEventProducerFactory bean is being called.");
        Map<String, Object> configProps = kafkaProperties.buildProducerProperties();

        log.info("DEBUG: Initial configProps from kafkaProperties.buildProducerProperties(): {}", configProps);

        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());


        // 클라이언트 ID 명시적 설정 (디버깅용)
        configProps.put(ProducerConfig.CLIENT_ID_CONFIG, "my-custom-producer");
        log.info("DEBUG: Final configProps before returning ProducerFactory: {}", configProps);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * insuranceEventProducerFactory를 기반으로 InsuranceEvent 메시지를 보내는 데 사용할 KafkaTemplate을 생성합니다.
     * @return KafkaTemplate<String, InsuranceEvent> 객체
     */
    @Bean
    public KafkaTemplate<String, InsuranceEvent> avroInsuranceEventKafkaTemplate(ProducerFactory<String, InsuranceEvent> insuranceEventProducerFactory) {
        log.info("DEBUG: avroInsuranceEventKafkaTemplate bean is being called.");
        return new KafkaTemplate<>(insuranceEventProducerFactory);
    }

    // contractEventProducerFactory() 메서드 제거
    // contractEventKafkaTemplate() 메서드 제거

    // /**
    //  * 일반 문자열 메시지를 전송하기 위한 ProducerFactory를 생성합니다.
    //  * @return ProducerFactory<String, String> 객체
    //  */
    // @Bean
    // public ProducerFactory<String, String> producerFactory() {
    //     Map<String, Object> configProps = new HashMap<>();
    //     configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    //     configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    //     configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    //     return new DefaultKafkaProducerFactory<>(configProps);
    // }

    // /**
    //  * producerFactory를 기반으로 일반 문자열 메시지를 보내는 데 사용할 KafkaTemplate을 생성합니다。
    //  * @return KafkaTemplate<String, String> 객체
    //  */
    // @Bean
    // public KafkaTemplate<String, String> kafkaTemplate() {
    //     return new KafkaTemplate<>(producerFactory());
    // }
}