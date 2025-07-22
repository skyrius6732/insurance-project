package com.example.insurance_project.controller;

import com.example.insurance_project.domain.Contract;
import com.example.insurance_project.kafka.KafkaProducerService;
import com.example.insurance_project.kafka.dto.ContractCreatedEvent;
import com.example.insurance_project.repository.ContractRepository;
import com.example.insurance_project.kafka.dto.SignContractRequest;
import com.example.insurance_project.kafka.dto.BatchSignContractRequest;
import com.example.insurance_project.kafka.dto.InsuranceEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

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
    private final ObjectMapper objectMapper; // ObjectMapper 주입

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
        String contractId = UUID.randomUUID().toString();

        // 2. Contract 엔티티를 생성하고 DB에 저장합니다.
        Contract newContract = Contract.builder()
                .contractId("CONTRACT-" + contractId)
                .customerId(customerId)
                .productId(productId)
                .build();
        contractRepository.save(newContract);
        log.info("Successfully saved contract to DB: {}", newContract.getContractId());

        ContractCreatedEvent contractCreatedEvent = ContractCreatedEvent.builder()
                .contractId("CONTRACT-" + contractId)
                .customerId(customerId)
                .productId(productId)
                .build();
        producerService.sendContractCreatedEvent(contractCreatedEvent); // ContractCreatedEvent 발행

        // InsuranceEvent 발행
        InsuranceEvent insuranceEvent = new InsuranceEvent(
                "EVENT-" + UUID.randomUUID().toString(), // 고유한 eventId 생성
                "CONTRACT_SIGNED", // 이벤트 타입
                newContract.getContractId(),
                newContract.getCustomerId(),
                objectMapper.createObjectNode() // 이벤트 상세 데이터를 JSON으로 구성
                        .put("productId", newContract.getProductId())
                        .put("timestamp", System.currentTimeMillis())
                        .toString()
        );
        producerService.sendInsuranceEvent(insuranceEvent);

        return "Contract " + newContract.getContractId() + " has been signed successfully. Both ContractCreatedEvent and InsuranceEvent published to Kafka.";
    }

    /**
     * 여러 신규 보험 계약을 일괄 체결하는 API 엔드포인트입니다.
     * 각 계약 건에 대해 개별적인 Kafka 이벤트를 발행합니다.
     * @param batchRequest 여러 계약 요청을 담은 본문
     * @return 처리 결과 요약 문자열
     */
    @PostMapping("/batch-sign")
    public String batchSignContracts(@RequestBody BatchSignContractRequest batchRequest) {
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        if (batchRequest.getContracts() == null || batchRequest.getContracts().isEmpty()) {
            return "No contracts provided in the batch request.";
        }

        batchRequest.getContracts().forEach(request -> {
            try {
                String customerId = request.getCustomerId();
                String productId = request.getProductId();
                String contractId = UUID.randomUUID().toString();

                Contract newContract = Contract.builder()
                        .contractId("CONTRACT-" + contractId)
                        .customerId(customerId)
                        .productId(productId)
                        .build();
                contractRepository.save(newContract);
                log.info("Successfully saved contract to DB: {}", newContract.getContractId());

                ContractCreatedEvent contractCreatedEvent = ContractCreatedEvent.builder()
                        .contractId("CONTRACT-" + contractId)
                        .customerId(customerId)
                        .productId(productId)
                        .build();
                producerService.sendContractCreatedEvent(contractCreatedEvent); // ContractCreatedEvent 발행

                InsuranceEvent insuranceEvent = new InsuranceEvent(
                        "EVENT-" + UUID.randomUUID().toString(), // 고유한 eventId 생성
                        "CONTRACT_SIGNED", // 이벤트 타입
                        newContract.getContractId(),
                        newContract.getCustomerId(),
                        objectMapper.createObjectNode()
                                .put("productId", newContract.getProductId())
                                .put("timestamp", System.currentTimeMillis())
                                .toString()
                );
                producerService.sendInsuranceEvent(insuranceEvent); // InsuranceEvent 발행

                successCount.incrementAndGet();
            } catch (Exception e) {
                log.error("Error processing batch contract for customer {}: {}", request.getCustomerId(), e.getMessage());
                failCount.incrementAndGet();
            }
        });

        return String.format("Batch contract signing completed. Success: %d, Failed: %d.", successCount.get(), failCount.get());
    }
}
