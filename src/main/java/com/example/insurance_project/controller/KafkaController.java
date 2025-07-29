package com.example.insurance_project.controller;

import com.example.insurance_project.kafka.KafkaProducerService;
import com.example.insurance_project.kafka.dto.InsuranceEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka")
public class KafkaController {

    private final KafkaProducerService producerService;

    @Autowired
    public KafkaController(KafkaProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping("/{key}/insurance-event")
    public String sendInsuranceEventWithKey(@PathVariable("key") String key, @RequestBody InsuranceEvent event) {
        producerService.sendInsuranceEventWithKey(key, event);
        return "InsuranceEvent with key sent to Kafka topic";
    }
}
