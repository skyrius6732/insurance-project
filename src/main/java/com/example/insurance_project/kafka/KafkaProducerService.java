package com.example.insurance_project.kafka;

// import com.example.insurance_project.kafka.dto.ContractCreatedEvent; // 제거
import com.example.insurance_project.kafka.dto.InsuranceEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    // Spring이 기본으로 설정해주는 문자열 전용 KafkaTemplate
    private final KafkaTemplate<String, String> kafkaTemplate;
    // KafkaProducerConfig에서 우리가 직접 설정한 ContractCreatedEvent 객체 전용 KafkaTemplate (제거)
    // private final KafkaTemplate<String, ContractCreatedEvent> contractEventKafkaTemplate;
    // ObjectMapper를 사용하여 객체를 JSON 문자열로 변환합니다.
    private final ObjectMapper objectMapper;

    /**
     * 범용적인 InsuranceEvent를 Kafka에 전송합니다.
     * @param event 전송할 InsuranceEvent 객체
     */
    public void sendInsuranceEvent(InsuranceEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            log.info("Produce InsuranceEvent: {}", eventJson);
            // policyNumber를 메시지 키로 사용
            this.kafkaTemplate.send(TOPIC_CONTRACT_EVENTS, event.getPolicyNumber(), eventJson);
        } catch (JsonProcessingException e) {
            log.error("Error serializing InsuranceEvent to JSON: {}", e.getMessage());
        }
    }

    /**
     * 계약 생성 이벤트(ContractCreatedEvent)를 Kafka에 전송합니다.
     * 이 메서드는 KafkaTemplate을 사용하여 객체를 직접 직렬화하는 예시입니다.
     * @param event 전송할 계약 생성 이벤트 객체
     */
    // public void sendContractCreatedEvent(ContractCreatedEvent event) { // 제거
    //     log.info("Produce ContractCreatedEvent (Object): {}", event.toString());
    //     // contract-events 토픽으로 이벤트 객체를 전송합니다.E
    //     // KafkaProducerConfig 설정 덕분에 이 객체는 자동으로 JSON으로 변환됩니다.
    //     // contractId를 메시지 키로 사용
    //     this.contractEventKafkaTemplate.send(TOPIC_CONTRACT_EVENTS, event.getContractId(), event);
    // }

    /**
     * 메시지 키를 포함하여 InsuranceEvent를 Kafka에 전송합니다.
     * @param key 메시지 키
     * @param event 전송할 InsuranceEvent 객체
     */
    public void sendInsuranceEventWithKey(String key, InsuranceEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            log.info("Produce InsuranceEvent with key: {} - {}", key, eventJson);
            this.kafkaTemplate.send(TOPIC_CONTRACT_EVENTS, key, eventJson);
        } catch (JsonProcessingException e) {
            log.error("Error serializing InsuranceEvent to JSON: {}", e.getMessage());
        }
    }
}
