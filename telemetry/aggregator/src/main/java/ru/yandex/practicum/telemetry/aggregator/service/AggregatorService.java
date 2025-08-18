package ru.yandex.practicum.telemetry.aggregator.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AggregatorService {

    Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        log.info("Обновление состояния для события сенсора: {}", event);

        SensorsSnapshotAvro snapshot = snapshots.computeIfAbsent(event.getHubId(), hubId -> {
            log.info("Создание нового снимка для hubId: {}", hubId);
            return SensorsSnapshotAvro.newBuilder()
                    .setHubId(hubId)
                    .setSensorsState(new HashMap<>())
                    .setTimestamp(event.getTimestamp())
                    .build();
        });

        SensorStateAvro oldState = snapshot.getSensorsState().get(event.getId());

        if (oldState != null) {
            log.info("Найдено существующее состояние для sensorId {}: {}", event.getId(), oldState);
            if (oldState.getTimestamp().isAfter(event.getTimestamp()) ||
                    oldState.getData().equals(event.getPayload())) {
                log.info("Обновление не требуется для sensorId {}. Старое состояние более актуально или данные не изменились.", event.getId());
                return Optional.empty();
            }
        } else {
            log.info("Существующее состояние не найдено для sensorId {}. Добавление нового состояния.", event.getId());
        }

        SensorStateAvro newSensorState = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();

        snapshot.getSensorsState().put(event.getId(), newSensorState);
        snapshot.setTimestamp(event.getTimestamp());

        log.info("Обновлено состояние для sensorId {}: {}", event.getId(), newSensorState);
        return Optional.of(snapshot);
    }
}