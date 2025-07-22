package com.example.insurance_project.kafka.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InsuranceEvent {
    private String eventId;
    private String eventType;
    private String policyNumber;
    private String customerId;
    private String eventData; // JSON string or other structured data

    @JsonCreator
    public InsuranceEvent(@JsonProperty("eventId") String eventId,
                          @JsonProperty("eventType") String eventType,
                          @JsonProperty("policyNumber") String policyNumber,
                          @JsonProperty("customerId") String customerId,
                          @JsonProperty("eventData") String eventData) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.policyNumber = policyNumber;
        this.customerId = customerId;
        this.eventData = eventData;
    }
}
