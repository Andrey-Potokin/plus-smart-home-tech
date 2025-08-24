package ru.yandex.practicum.telemetry.analyzer.processor;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.analyzer.kafka.AnalyzerClient;
import ru.yandex.practicum.telemetry.analyzer.service.AnalyzerServiceImpl;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HubEventProcessor implements Runnable {

    AnalyzerClient client;
    AnalyzerServiceImpl service;

    private static final String TELEMETRY_HUBS_KAFKA_TOPIC = "telemetry.hubs.v1";

    @Override
    public void run() {
        Consumer<Void, HubEventAvro> consumer = client.getHubEventConsumer();

        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        consumer.subscribe(List.of(TELEMETRY_HUBS_KAFKA_TOPIC));

        try {
            while (true) {
                ConsumerRecords<Void, HubEventAvro> records = consumer.poll(Duration.ofSeconds(5));

                records.forEach(record -> {
                    try {
                        service.handleHubEvent(record.value());
                    } catch (Exception e) {
                        log.error("Ошибка при обработке записи: {}", e.getMessage());
                    }
                });
            }
        } catch (WakeupException e) {
            log.info("Consumer wakeup вызван. Завершение работы.");
        } catch (Exception e) {
            log.error("Ошибка в HubEventProcessor: {}", e.getMessage());
        } finally {
            client.stop();
        }
    }
}