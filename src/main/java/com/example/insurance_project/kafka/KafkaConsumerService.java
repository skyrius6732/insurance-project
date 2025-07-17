package com.example.insurance_project.kafka;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics="test-topic", groupId = "skyrius")
    public void consume(String message) throws IOException{
        System.out.println(String.format("Consumed message : %s", message));
    }
}
