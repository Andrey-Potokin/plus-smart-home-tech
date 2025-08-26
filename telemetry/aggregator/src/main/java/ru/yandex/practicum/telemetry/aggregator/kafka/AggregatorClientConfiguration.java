package ru.yandex.practicum.telemetry.aggregator.kafka;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "spring.kafka")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AggregatorClientConfiguration {

    @Bean
    AggregatorClient getClient() {
        return new AggregatorClient() {

            Consumer<Void, SensorEventAvro> consumer;
            Producer<Void, SensorsSnapshotAvro> producer;

            @Value("${spring.kafka.consumer.group-id}")
            String groupId;

            @Value("${spring.kafka.consumer.bootstrap-servers}")
            String consumerBootstrapServers;

            @Value("${spring.kafka.consumer.key-deserializer}")
            String keyDeserializer;

            @Value("${spring.kafka.consumer.value-deserializer}")
            String valueDeserializer;

            @Value("${spring.kafka.producer.bootstrap-servers}")
            String producerBootstrapServers;

            @Value("${spring.kafka.producer.key-serializer}")
            String keySerializer;

            @Value("${spring.kafka.producer.value-serializer}")
            String valueSerializer;

            @Override
            public Consumer<Void, SensorEventAvro> getConsumer() {
                if (consumer == null) {
                    initConsumer();
                }
                return consumer;
            }

            private void initConsumer() {
                Properties config = new Properties();
                config.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerBootstrapServers);
                config.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
                config.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
                config.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
                consumer = new KafkaConsumer<>(config);
            }

            @Override
            public Producer<Void, SensorsSnapshotAvro> getProducer() {
                if (producer == null) {
                    initProducer();
                }
                return producer;
            }

            private void initProducer() {
                Properties config = new Properties();
                config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producerBootstrapServers);
                config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
                config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);

                producer = new KafkaProducer<>(config);
            }

            @Override
            public void stop() {
                if (consumer != null) {
                    consumer.close();
                }

                if (producer != null) {
                    producer.close();
                }
            }
        };
    }
}