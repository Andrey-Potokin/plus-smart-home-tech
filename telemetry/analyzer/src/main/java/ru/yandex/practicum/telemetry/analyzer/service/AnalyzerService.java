package ru.yandex.practicum.telemetry.analyzer.service;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public interface AnalyzerService {
    void handleSnapshot(SensorsSnapshotAvro value);

    void handleHubEvent(HubEventAvro value);
}