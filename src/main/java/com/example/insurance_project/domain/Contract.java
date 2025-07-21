
package com.example.insurance_project.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contractId;
    private String customerId;
    private String productId;
    private LocalDateTime createdAt;

    @Builder
    public Contract(String contractId, String customerId, String productId) {
        this.contractId = contractId;
        this.customerId = customerId;
        this.productId = productId;
        this.createdAt = LocalDateTime.now();
    }
}
