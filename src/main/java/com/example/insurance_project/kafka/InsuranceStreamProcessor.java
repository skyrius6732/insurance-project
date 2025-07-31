package com.example.insurance_project.kafka;

import com.example.insurance_project.kafka.avro.InsuranceEvent;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Configuration
@EnableKafkaStreams
public class InsuranceStreamProcessor {

    @Autowired
    private KafkaStreamsConfiguration kafkaStreamsConfiguration;

    @Bean
    public KStream<String, String> kStream(StreamsBuilder streamsBuilder) {
        // 1. Avro Serde (Serializer/Deserializer) 설정
        // 스키마 레지스트리 URL을 스트림 처리기에도 알려주어야 합니다.
        final Map<String, String> serdeConfig = Collections.singletonMap(
                "schema.registry.url",
                (String) kafkaStreamsConfiguration.asProperties().get("schema.registry.url")
        );

        final Serde<InsuranceEvent> insuranceEventSerde = new SpecificAvroSerde<>();
        insuranceEventSerde.configure(serdeConfig, false);

        // 2. 입력 스트림 생성
        // 'contract-events' 토픽에서 메시지를 읽어옵니다.
        KStream<String, InsuranceEvent> sourceStream = streamsBuilder
                .stream("contract-events", Consumed.with(Serdes.String(), insuranceEventSerde));

        // 3. 데이터 변환 (가공)
        // InsuranceEvent 객체에서 필요한 정보만 추출하여 간단한 문자열로 변환합니다.
        KStream<String, String> summaryStream = sourceStream.mapValues(
                event -> {
                    String summary = String.format("Policy Summary: [PolicyNumber=%s, CustomerId=%s, AgentId=%s]",
                            event.getPolicyNumber(),
                            event.getCustomerId(),
                            event.getAgentId()
                    );
                    log.info("Processing stream event. Key: {}, Summary: {}", event.getPolicyNumber(), summary);
                    return summary;
                }
        );

        // 4. 출력 스트림으로 전송
        // 변환된 문자열 메시지를 'policy-summary-events' 토픽으로 보냅니다.
        summaryStream.to("policy-summary-events", Produced.with(Serdes.String(), Serdes.String()));

        log.info("Kafka Streams processor initialized. Reading from 'contract-events' and writing to 'policy-summary-events'.");

        return summaryStream;
    }
}
