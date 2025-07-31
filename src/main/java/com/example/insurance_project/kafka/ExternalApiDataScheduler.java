package com.example.insurance_project.kafka;

import com.example.insurance_project.kafka.avro.InsuranceEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalApiDataScheduler {

    private final KafkaProducerService kafkaProducerService;
    private final WebClient.Builder webClientBuilder;

    @Value("${faker.api.url}")
    private String fakerApiUrl;

    // 10초마다 실행
    @Scheduled(fixedRate = 10000)
    public void fetchAndProduceData() {
        log.info("Fetching data from Faker API...");

        fetchFakeInsuranceData()
                .flatMap(this::transformToInsuranceEvent)
                .doOnNext(kafkaProducerService::sendInsuranceEvent)
                .subscribe(
                        event -> log.debug("Successfully sent event: {}", event.getPolicyNumber()),
                        error -> log.error("Error during data fetch and produce", error),
                        () -> log.info("Finished fetching and producing data for this schedule.")
                );
    }

    private Flux<Map<String, Object>> fetchFakeInsuranceData() {
        return webClientBuilder.build()
                .get()
                .uri(fakerApiUrl, uriBuilder -> uriBuilder
                        .queryParam("_quantity", 10)
                        .queryParam("customerId", "uuid")
                        .queryParam("policyNumber", "ean13")
                        .queryParam("agentId", "name")
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .flatMapMany(response -> {
                    if ("OK".equals(response.get("status"))) {
                        List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
                        return Flux.fromIterable(data);
                    } else {
                        return Flux.error(new RuntimeException("Faker API returned an error: " + response));
                    }
                });
    }

    private Flux<InsuranceEvent> transformToInsuranceEvent(Map<String, Object> data) {
        try {
            InsuranceEvent event = InsuranceEvent.newBuilder()
                    .setEventId("EVENT-" + UUID.randomUUID().toString())
                    .setEventType("EXTERNAL_CONTRACT_SIGNED")
                    .setCustomerId((String) data.get("customerId"))
                    .setPolicyNumber((String) data.get("policyNumber"))
                    .setAgentId((String) data.get("agentId"))
                    .setEventData("{}") // 추가 데이터 없음을 명시
                    .setEventTimestamp(Instant.now().toEpochMilli())
                    .build();
            return Flux.just(event);
        } catch (Exception e) {
            log.error("Failed to transform data to InsuranceEvent: {}", data, e);
            return Flux.empty();
        }
    }
}
