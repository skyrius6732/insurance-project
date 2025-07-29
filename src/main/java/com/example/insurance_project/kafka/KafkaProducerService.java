package com.example.insurance_project.kafka;

import com.example.insurance_project.kafka.avro.InsuranceEvent;
import com.fasterxml.jackson.core.JsonProcessingException; // JsonProcessingException 임포트
import com.fasterxml.jackson.databind.ObjectMapper; // ObjectMapper 임포트
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Kafka에 메시지를 발행(Produce)하는 역할을 담당하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 생성해주는 Lombok 어노테이션
public class KafkaProducerService {

    // 로거(Logger) 객체 생성
    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);

    // 계약 생성 이벤트를 보낼 토픽 이름
    private static final String TOPIC_CONTRACT_EVENTS = "contract-events";

    // Spring Kafka가 application.properties 설정을 기반으로 자동 구성해주는 KafkaTemplate
    // 이제 Value 타입은 Avro가 생성한 InsuranceEvent 클래스가 됩니다.
    private final KafkaTemplate<String, InsuranceEvent> insuranceEventKafkaTemplate; // 빈 이름 변경

    // ObjectMapper를 사용하여 객체를 JSON 문자열로 변환합니다. (학습용으로 복원)
    private final ObjectMapper objectMapper;

    /**
     * 범용적인 InsuranceEvent를 Kafka에 전송합니다. (Avro 버전)
     *
     * @param event 전송할 InsuranceEvent 객체
     */
    public void sendInsuranceEvent(InsuranceEvent event) {
        log.info("Produce Avro InsuranceEvent: {}", event.toString());
        // policyNumber를 메시지 키로 사용합니다.
        // KafkaAvroSerializer가 event 객체를 Avro 포맷으로 직렬화합니다.
        this.insuranceEventKafkaTemplate.send(TOPIC_CONTRACT_EVENTS, event.getPolicyNumber().toString(), event);
    }

    /**
     * 범용적인 InsuranceEvent를 Kafka에 전송합니다. (JSON 버전 - 학습용 주석 처리)
     * @param event 전송할 InsuranceEvent 객체
     */
    // public void sendInsuranceEventJson(InsuranceEvent event) {
    //     try {
    //         String eventJson = objectMapper.writeValueAsString(event);
    //         log.info("Produce InsuranceEvent (JSON): {}", eventJson);
    //         // policyNumber를 메시지 키로 사용
    //         this.kafkaTemplate.send(TOPIC_CONTRACT_EVENTS, event.getPolicyNumber(), eventJson);
    //     } catch (JsonProcessingException e) {
    //         log.error("Error serializing InsuranceEvent to JSON: {}", e.getMessage());
    //     }
    // }

    /**
     * 메시지 키를 포함하여 InsuranceEvent를 Kafka에 전송합니다. (Avro 버전)
     *
     * @param key   메시지 키
     * @param event 전송할 InsuranceEvent 객체
     */
    public void sendInsuranceEventWithKey(String key, InsuranceEvent event) {
        log.info("Produce Avro InsuranceEvent with key: {} - {}", key, event.toString());
        this.insuranceEventKafkaTemplate.send(TOPIC_CONTRACT_EVENTS, key, event);
    }


    /**
     * 메시지 키를 포함하여 InsuranceEvent를 Kafka에 전송합니다. (JSON 버전 - 학습용 주석 처리)
     * @param key 메시지 키
     * @param event 전송할 InsuranceEvent 객체
     */
    // public void sendInsuranceEventWithKeyJson(String key, InsuranceEvent event) {
    //     try {
    //         String eventJson = objectMapper.writeValueAsString(event);
    //         log.info("Produce InsuranceEvent with key (JSON): {} - {}", key, eventJson);
    //         this.kafkaTemplate.send(TOPIC_CONTRACT_EVENTS, key, eventJson);
    //     } catch (JsonProcessingException e) {
    //         log.error("Error serializing InsuranceEvent to JSON: {}", e.getMessage());
    //     }
    // }
}

