package com.example.insurance_project.controller;

import com.example.insurance_project.domain.Contract;
import com.example.insurance_project.kafka.dto.ContractCreatedEvent;
import com.example.insurance_project.kafka.KafkaProducerService;
import com.example.insurance_project.repository.ContractRepository;
import com.example.insurance_project.kafka.dto.SignContractRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

/**
 * 계약(Contract) 관련 API 요청을 처리하는 컨트롤러입니다.
 */
@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private static final Logger log = LoggerFactory.getLogger(ContractController.class);

    private final ContractRepository contractRepository;
    private final KafkaProducerService producerService;

    /**
     * 신규 보험 계약을 체결하는 API 엔드포인트입니다.
     * @param request 계약 요청 본문 (고객 ID, 상품 ID 포함)
     * @return 생성된 계약 정보 문자열
     */
    @PostMapping("/sign")
    public String signContract(@RequestBody SignContractRequest request) {
        // 1. 요청 본문(Request Body)에서 고객 ID와 상품 ID를 받아옵니다.
        String customerId = request.getCustomerId();
        String productId = request.getProductId();

        // 2. Contract 엔티티를 생성하고 DB에 저장합니다.
        Contract newContract = Contract.builder()
                .contractId("CONTRACT-" + UUID.randomUUID().toString())
                .customerId(customerId)
                .productId(productId)
                .build();
        contractRepository.save(newContract);
        log.info("Successfully saved contract to DB: {}", newContract.getContractId());

        // 3. DB 저장 후, 후속 처리를 위해 Kafka에 이벤트를 발행합니다.
        ContractCreatedEvent event = ContractCreatedEvent.builder()
                .contractId(newContract.getContractId())
                .customerId(newContract.getCustomerId())
                .productId(newContract.getProductId())
                .timestamp(System.currentTimeMillis())
                .build();
        producerService.sendContractCreatedEvent(event);

        return "Contract " + newContract.getContractId() + " has been signed successfully. Event published to Kafka.";
    }
}
