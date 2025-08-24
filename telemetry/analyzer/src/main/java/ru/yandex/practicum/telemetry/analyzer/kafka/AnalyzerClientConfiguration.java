package ru.yandex.practicum.telemetry.analyzer.kafka;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Properties;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnalyzerClientConfiguration {

    @Bean
    AnalyzerClient getClient() {
        return new AnalyzerClient() {

            Consumer<Void, SensorsSnapshotAvro> snapshotConsumer;
            Consumer<Void, HubEventAvro> hubEventConsumer;

            @Value("${spring.kafka.consumer.snapshot.bootstrap-servers}")
            String consumerSnapshotBootstrapServers;

            @Value("${spring.kafka.consumer.snapshot.key-deserializer}")
            String snapshotKeyDeserializer;

            @Value("${spring.kafka.consumer.snapshot.value-deserializer}")
            String snapshotValueDeserializer;

            @Value("${spring.kafka.consumer.snapshot.group-id}")
            String snapshotGroupId;

            @Value("${spring.kafka.consumer.hub-event.bootstrap-servers}")
            String consumerHubEventBootstrapServers;

            @Value("${spring.kafka.consumer.hub-event.key-deserializer}")
            String hubEventKeyDeserializer;

            @Value("${spring.kafka.consumer.hub-event.value-deserializer}")
            String hubEventValueDeserializer;

            @Value("${spring.kafka.consumer.hub-event.group-id}")
            String hubEventGroupId;

            @Override
            public Consumer<Void, SensorsSnapshotAvro> getSnapshotConsumer() {
                if (snapshotConsumer == null) {
                    initSnapshotConsumer();
                }
                return snapshotConsumer;
            }

            @Override
            public Consumer<Void, HubEventAvro> getHubEventConsumer() {
                if (hubEventConsumer == null) {
                    initHubEventConsumer();
                }
                return hubEventConsumer;
            }

            @Override
            public void stop() {
                if (snapshotConsumer != null) {
                    snapshotConsumer.close();
                }
                if (hubEventConsumer != null) {
                    hubEventConsumer.close();
                }
            }

            private void initSnapshotConsumer() {
                Properties config = new Properties();
                config.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerSnapshotBootstrapServers);
                config.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, snapshotKeyDeserializer);
                config.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, snapshotValueDeserializer);
                config.setProperty(ConsumerConfig.GROUP_ID_CONFIG, snapshotGroupId);
                snapshotConsumer = new KafkaConsumer<>(config);
            }

            private void initHubEventConsumer() {
                Properties config = new Properties();
                config.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerHubEventBootstrapServers);
                config.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, hubEventKeyDeserializer);
                config.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, hubEventValueDeserializer);
                config.setProperty(ConsumerConfig.GROUP_ID_CONFIG, hubEventGroupId);
                hubEventConsumer = new KafkaConsumer<>(config);
            }
        };
    }
}