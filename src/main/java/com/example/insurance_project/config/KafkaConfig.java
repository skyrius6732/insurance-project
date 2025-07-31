package com.example.insurance_project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;
import org.apache.kafka.common.TopicPartition;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;

import org.springframework.context.annotation.Lazy;

@Configuration
@Lazy
public class KafkaConfig {

    // KafkaTemplate을 직접 빈으로 정의하므로 생성자 주입 제거
    // private final KafkaTemplate<Object, Object> kafkaTemplate;
    // public KafkaConfig(KafkaTemplate<Object, Object> kafkaTemplate) {
    //     this.kafkaTemplate = kafkaTemplate;
    // }

    @Bean
    public ProducerFactory<Object, Object> producerFactory(KafkaProperties kafkaProperties) {
        return new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties());
    }

    @Bean
    public KafkaTemplate<Object, Object> kafkaTemplate(ProducerFactory<Object, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> kafkaTemplate) { // KafkaTemplate을 주입받도록 변경
        // DeadLetterPublishingRecoverer를 사용하여 메시지를 DLQ로 보냅니다.
        // 첫 번째 인자는 KafkaTemplate, 두 번째 인자는 DLT 토픽을 결정하는 함수입니다.
        // 여기서는 원본 토픽명에 "-dlt" 접미사를 붙여 DLQ 토픽을 지정합니다.
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (r, e) -> new TopicPartition(r.topic() + "-dlt", r.partition()));

        // FixedBackOff를 사용하여 재시도 정책을 정의합니다.
        // 1000ms (1초) 간격으로 최대 2번 재시도 (총 3번 시도)
        FixedBackOff fixedBackOff = new FixedBackOff(1000L, 2L);

        // DefaultErrorHandler를 생성하고 Recoverer와 BackOff를 설정합니다.
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, fixedBackOff);

        // 역직렬화 예외를 처리하도록 설정 (ErrorHandlingDeserializer와 함께 작동)
        // 이 설정이 없으면 DefaultErrorHandler는 역직렬화 예외를 직접 처리하지 못합니다.
        errorHandler.addNotRetryableExceptions(
            org.apache.kafka.common.errors.SerializationException.class,
            org.springframework.kafka.support.serializer.DeserializationException.class
        );

        return errorHandler;
    }

    @Bean
    public ConsumerFactory<Object, Object> consumerFactory(KafkaProperties kafkaProperties) {
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties());
    }

    // 모든 KafkaListener에 이 errorHandler를 적용하려면
    // ConcurrentKafkaListenerContainerFactory를 커스터마이징해야 합니다。
    // Spring Boot가 기본적으로 생성하는 factory를 오버라이드합니다。
    @Bean
    public ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactory(
            ConsumerFactory<Object, Object> consumerFactory,
            DefaultErrorHandler errorHandler) { // 우리가 정의한 errorHandler 빈을 주입받습니다.
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler); // 우리가 정의한 errorHandler를 설정합니다。
        return factory;
    }
}
