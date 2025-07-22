package com.example.insurance_project.kafka;

import com.example.insurance_project.kafka.dto.ContractCreatedEvent;
import com.example.insurance_project.kafka.dto.InsuranceEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka로부터 메시지를 구독(Consume)하여 처리하는 서비스들을 포함합니다.
 * 각 Consumer는 독립적인 groupId를 가짐으로써 동일한 이벤트를 각각 처리할 수 있습니다.
 */
@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 생성해주는 Lombok 어노테이션
public class KafkaConsumerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final ObjectMapper objectMapper;

    /**
     * 알림 서비스를 위한 Consumer입니다.
     * contract-events 토픽을 구독하며, groupId는 notification-group입니다.
     * @param event 수신한 계약 생성 이벤트 객체
     */
    @KafkaListener(topics = "contract-events", groupId = "notification-group", containerFactory = "contractEventKafkaListenerContainerFactory")
    public void consumeForNotification(ContractCreatedEvent event) {
        log.info("[Notification-Consumer] Received contract event: {}", event.toString());
        log.info("-> Sending email to customer {} for contract {}", event.getCustomerId(), event.getContractId());
        // TODO: 실제 이메일 또는 카카오톡 알림 발송 로직 구현
    }

    /**
     * 문서 생성을 위한 Consumer입니다.
     * contract-events 토픽을 구독하며, groupId는 document-group입니다.
     * @param event 수신한 계약 생성 이벤트 객체
     */
    @KafkaListener(topics = "contract-events", groupId = "document-group", containerFactory = "contractEventKafkaListenerContainerFactory")
    public void consumeForDocumentation(ContractCreatedEvent event) {
        log.info("[Document-Consumer] Received contract event: {}", event.toString());
        log.info("-> Generating PDF document for contract {}", event.getContractId());
        // TODO: 실제 PDF 문서 생성 로직 구현
    }


    /**
     * 알림 서비스를 위한 Consumer입니다.
     * contract-events 토픽을 구독하며, groupId는 notification-group입니다.
     * @param message 수신한 JSON 문자열 메시지
     */
    @KafkaListener(topics = "contract-events", groupId = "notification-group")
    public void consumeForNotification(String message) {
        try {
            InsuranceEvent event = objectMapper.readValue(message, InsuranceEvent.class);
            log.info("[Notification-Consumer] Received InsuranceEvent: {}", event.toString());
            log.info("-> Sending email to customer {} for contract {}", event.getCustomerId(), event.getPolicyNumber());
            // TODO: 실제 이메일 또는 카카오톡 알림 발송 로직 구현
        } catch (JsonProcessingException e) {
            log.error("[Notification-Consumer] Error deserializing message: {}", e.getMessage());
        }
    }

    /**
     * 문서 생성을 위한 Consumer입니다.
     * contract-events 토픽을 구독하며, groupId는 document-group입니다.
     * @param message 수신한 JSON 문자열 메시지
     */
    @KafkaListener(topics = "contract-events", groupId = "document-group")
    public void consumeForDocumentation(String message) {
        try {
            InsuranceEvent event = objectMapper.readValue(message, InsuranceEvent.class);
            log.info("[Document-Consumer] Received InsuranceEvent: {}", event.toString());
            log.info("-> Generating PDF document for contract {}", event.getPolicyNumber());
            // TODO: 실제 PDF 문서 생성 로직 구현
        } catch (JsonProcessingException e) {
            log.error("[Document-Consumer] Error deserializing message: {}", e.getMessage());
        }
    }

}