package ru.yandex.practicum.telemetry.analyzer.service;

import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public interface AnalyzerService {
    void check(SensorsSnapshotAvro value);
}