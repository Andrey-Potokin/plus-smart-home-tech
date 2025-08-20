package ru.yandex.practicum.telemetry.analyzer.processor;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.analyzer.kafka.AnalyzerClient;
import ru.yandex.practicum.telemetry.analyzer.service.AnalyzerServiceImpl;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SnapshotProcessor {

    AnalyzerClient client;
    AnalyzerServiceImpl service;

    private static final String TELEMETRY_SNAPSHOT_KAFKA_TOPIC = "telemetry.snapshots.v1";

    public void start() {
        Consumer<Void, SensorsSnapshotAvro> consumer = client.getSnapshotConsumer();

        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        consumer.subscribe(List.of(TELEMETRY_SNAPSHOT_KAFKA_TOPIC));

        try {
            while (true) {
                ConsumerRecords<Void, SensorsSnapshotAvro> records = consumer.poll(Duration.ofSeconds(5));

                records.forEach(record -> {
                    try {
                        // TODO дописать
                        service.check(record.value());
                    } catch (Exception e) {
                        log.error("Ошибка при обработке записи: {}", e.getMessage());
                    }
                });
            }
        } catch (WakeupException e) {
            log.info("Consumer wakeup вызван. Завершение работы.");
        } catch (Exception e) {
            log.error("Ошибка в SnapshotProcessor: {}", e.getMessage());
        } finally {
            client.stop();
        }
    }
}