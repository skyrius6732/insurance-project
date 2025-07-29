package com.example.insurance_project.kafka;


import com.example.insurance_project.kafka.avro.InsuranceEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

/**
 * Kafka로부터 메시지를 구독(Consume)하여 처리하는 서비스들을 포함합니다.
 * 각 Consumer는 독립적인 groupId를 가짐으로써 동일한 이벤트를 각각 처리할 수 있습니다.
 */
@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 생성해주는 Lombok 어노테이션
public class KafkaConsumerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);

    /**
     * 알림 서비스를 위한 Consumer입니다.
     * contract-events 토픽을 구독하며, groupId는 notification-group입니다.
     * @param event 수신한 계약 생성 이벤트 객체
     */
    // @KafkaListener(topics = "contract-events", groupId = "notification-group", containerFactory = "contractEventKafkaListenerContainerFactory")
    // public void consumeForNotification(ContractCreatedEvent event) {
    //     log.info("[Notification-Consumer] Received contract event: {}", event.toString());
    //     log.info("-> Sending email to customer {} for contract {}", event.getCustomerId(), event.getContractId());
    //     // TODO: 실제 이메일 또는 카카오톡 알림 발송 로직 구현
    // }

    /**
     * 문서 생성을 위한 Consumer입니다.
     * contract-events 토픽을 구독하며, groupId는 document-group입니다.
     * @param event 수신한 계약 생성 이벤트 객체
     */
    // @KafkaListener(topics = "contract-events", groupId = "document-group", containerFactory = "contractEventKafkaListenerContainerFactory")
    // public void consumeForDocumentation(ContractCreatedEvent event) {
    //     log.info("[Document-Consumer] Received contract event: {}", event.toString());
    //     log.info("-> Generating PDF document for contract {}", event.getContractId());
    //     // TODO: 실제 PDF 문서 생성 로직 구현
    // }


    /**
     * 알림 서비스를 위한 Consumer입니다.
     * contract-events 토픽을 구독하며, groupId는 notification-group-avro 입니다.
     * KafkaAvroDeserializer가 메시지를 InsuranceEvent 객체로 자동 변환해줍니다.
     * @param event 수신한 Avro InsuranceEvent 객체
     * @param key 수신한 메시지의 키
     */
    @KafkaListener(topics = "contract-events", groupId = "notification-group-avro")
    public void consumeForNotification(InsuranceEvent event, @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("[Notification-Consumer] Received Avro InsuranceEvent with key {}: {}", key, event.toString());
        log.info("-> Sending email to customer {} for contract {}. Agent: {}", event.getCustomerId(), event.getPolicyNumber(), event.getAgentId());
        // TODO: 실제 이메일 또는 카카오톡 알림 발송 로직 구현
    }

    /**
     * 문서 생성을 위한 Consumer입니다.
     * contract-events 토픽을 구독하며, groupId는 document-group-avro 입니다.
     * @param event 수신한 Avro InsuranceEvent 객체
     * @param key 수신한 메시지의 키
     */
    // @KafkaListener(topics = "contract-events", groupId = "document-group-avro")
    // public void consumeForDocumentation(InsuranceEvent event, @Header(KafkaHeaders.RECEIVED_KEY) String key) {
    //     log.info("[Document-Consumer] Received Avro InsuranceEvent with key {}: {}", key, event.toString());
    //     log.info("-> Generating PDF document for contract {}", event.getPolicyNumber());
    //     // TODO: 실제 PDF 문서 생성 로직 구현
    // }
}