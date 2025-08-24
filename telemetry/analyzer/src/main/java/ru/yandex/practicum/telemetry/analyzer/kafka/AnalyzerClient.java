package ru.yandex.practicum.telemetry.analyzer.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public interface AnalyzerClient {

    Consumer<Void, SensorsSnapshotAvro> getSnapshotConsumer();

    Consumer<Void, HubEventAvro> getHubEventConsumer();

    void stop();
}