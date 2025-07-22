package com.example.insurance_project.kafka.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class BatchSignContractRequest {
    private List<SignContractRequest> contracts;
}
