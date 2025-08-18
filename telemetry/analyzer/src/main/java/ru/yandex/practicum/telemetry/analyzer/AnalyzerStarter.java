package ru.yandex.practicum.telemetry.analyzer;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.telemetry.analyzer.service.AnalyzerService;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnalyzerStarter {

    AnalyzerService service;

    private static final String TELEMETRY_SNAPSHOT_KAFKA_TOPIC = "telemetry.snapshots.v1";

    public void run() {
    }
}