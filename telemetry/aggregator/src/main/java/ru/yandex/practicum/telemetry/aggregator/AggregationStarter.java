package ru.yandex.practicum.telemetry.aggregator;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.aggregator.kafka.AggregatorClient;
import ru.yandex.practicum.telemetry.aggregator.service.AggregatorServiceImpl;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AggregationStarter {

    AggregatorClient aggregatorClient;
    AggregatorServiceImpl service;

    private static final String TELEMETRY_SENSORS_KAFKA_TOPIC = "telemetry.sensors.v1";
    private static final String TELEMETRY_SNAPSHOT_KAFKA_TOPIC = "telemetry.snapshots.v1";

    public void start() {
        Consumer<Void, SensorEventAvro> consumer = aggregatorClient.getConsumer();
        Producer<Void, SensorsSnapshotAvro> producer = aggregatorClient.getProducer();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            consumer.wakeup();
            producer.close();
        }));

        consumer.subscribe(List.of(TELEMETRY_SENSORS_KAFKA_TOPIC));

        try {
            while (true) {
                ConsumerRecords<Void, SensorEventAvro> records = consumer.poll(Duration.ofSeconds(5));

                records.forEach(record -> {
                    try {
                        Optional<SensorsSnapshotAvro> optionalSnapshot = service.updateState(record.value());

                        optionalSnapshot.ifPresent(snapshot -> {
                            log.info("Отправка для хаба={}, снимок={}, в топик={}", snapshot.getHubId(), snapshot, TELEMETRY_SNAPSHOT_KAFKA_TOPIC);

                            producer.send(new ProducerRecord<>(TELEMETRY_SNAPSHOT_KAFKA_TOPIC, snapshot),
                                    (metadata, exception) -> {
                                        if (exception != null) {
                                            log.error("Ошибка при отправке сообщения в топик {}: {}",
                                                    TELEMETRY_SNAPSHOT_KAFKA_TOPIC, exception.getMessage());
                                        } else {
                                            log.info("Сообщение отправлено в топик {} с offset {}",
                                                    TELEMETRY_SNAPSHOT_KAFKA_TOPIC, metadata.offset());
                                        }
                                    });
                        });
                    } catch (Exception e) {
                        log.error("Ошибка при обработке записи: {}", e.getMessage());
                    }
                });
            }
        } catch (WakeupException e) {
            log.info("Consumer wakeup вызван. Завершение работы.");
        } catch (Exception e) {
            log.error("Ошибка в AggregationStarter: {}", e.getMessage());
        } finally {
            aggregatorClient.stop();
        }
    }
}