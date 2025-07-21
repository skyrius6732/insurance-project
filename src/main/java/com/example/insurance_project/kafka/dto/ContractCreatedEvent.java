package com.example.insurance_project.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContractCreatedEvent {
    private String contractId;
    private String customerId;
    private String productId;
    private Long timestamp;
}
