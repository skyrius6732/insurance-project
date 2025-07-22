package com.example.insurance_project.kafka.event;

import lombok.*;

import java.time.LocalDateTime;


@Getter @Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceContractEvent {
    private String contractId;
    private String customerId;
    private String productName;
    private double premium;
    private LocalDateTime contractDate;
    private String eventType; // "CONTRACT_CREATED", "CLAIM_FILED", "NOTIFICATION_SENT" 등



}
