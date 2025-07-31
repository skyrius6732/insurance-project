package com.example.insurance_project.kafka.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SignContractRequest {
    private String customerId;
    private String productId;
    private String policyNumber;
}
