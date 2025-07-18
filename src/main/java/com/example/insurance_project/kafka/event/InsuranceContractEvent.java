package com.example.insurance_project.kafka.event;

import java.time.LocalDateTime;

public class InsuranceContractEvent {
    private String contractId;
    private String customerId;
    private String productName;
    private double premium;
    private LocalDateTime contractDate;
    private String eventType; // "CONTRACT_CREATED", "CLAIM_FILED", "NOTIFICATION_SENT" 등

    // Constructors, getters, setters
    public InsuranceContractEvent() {}

    public InsuranceContractEvent(String contractId, String customerId, String productName, double premium, LocalDateTime contractDate, String eventType) {
        this.contractId = contractId;
        this.customerId = customerId;
        this.productName = productName;
        this.premium = premium;
        this.contractDate = contractDate;
        this.eventType = eventType;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPremium() {
        return premium;
    }

    public void setPremium(double premium) {
        this.premium = premium;
    }

    public LocalDateTime getContractDate() {
        return contractDate;
    }

    public void setContractDate(LocalDateTime contractDate) {
        this.contractDate = contractDate;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

}
