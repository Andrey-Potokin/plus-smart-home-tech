package ru.yandex.practicum.telemetry.aggregator.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AggregatorService {

    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        log.info("Событие: {}", event);
        Instant eventTimestamp = Instant.ofEpochMilli(event.getTimestamp());
        log.info("Метка времени: {}", eventTimestamp);


        Optional<SensorsSnapshotAvro> result = Optional.ofNullable(snapshots.compute(event.getHubId(), (hubId, snapshot) -> {
            if (snapshot == null) {
                log.info("Создание нового снимка для hubId: {} с событием: {}", event.getHubId(), event);
                return createNewSnapshot(event, eventTimestamp);
            } else {
                log.info("Обновление существующего снимка для hubId: {} с событием: {}", event.getHubId(), event);
                return updateExistingSnapshot(snapshot, event, eventTimestamp);
            }
        }));

        if (result.isPresent()) {
            log.info("Возвращаемый снимок для hubId {}: {}", event.getHubId(), result.get());
        } else {
            log.info("Возвращается пустой результат для hubId {}", event.getHubId());
        }

        return result;
    }

    private SensorsSnapshotAvro createNewSnapshot(SensorEventAvro event, Instant eventTimestamp) {
        log.info("В метод создания поступили событие {} и метка времени {}", event, eventTimestamp);

        SensorStateAvro sensorState = createSensorState(event, eventTimestamp);
        Map<String, SensorStateAvro> sensorsStates = new HashMap<>();
        sensorsStates.put(event.getId(), sensorState);

        log.info("Создан новый снимок состояния сенсоров: {}", sensorsStates);

        SensorsSnapshotAvro snapshot = SensorsSnapshotAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(eventTimestamp)
                .setSensorsState(sensorsStates)
                .build();

        log.info("Возвращаемый снимок: {}", snapshot);

        return snapshot;
    }

    private SensorsSnapshotAvro updateExistingSnapshot(SensorsSnapshotAvro snapshot, SensorEventAvro event,
                                                       Instant eventTimestamp) {
        log.info("В метод обновления поступил снимок {}, событие {} и метка времени {}", snapshot, event, eventTimestamp);
        Map<String, SensorStateAvro> sensorsStates = snapshot.getSensorsState();
        log.info("Из мапы получен набор событий {}", sensorsStates);

        if (sensorsStates.containsKey(event.getId())) {
            SensorStateAvro oldState = sensorsStates.get(event.getId());
            log.info("oldState присвоено значение = {}", oldState);

            if (oldState.getTimestamp().isAfter(eventTimestamp) || oldState.getData().equals(event.getPayload())) {
                log.warn("Игнорирование события для hubId: {} и id сенсора: {} - устаревшее состояние или одинаковые данные",
                        event.getHubId(), event.getId());
                return null;
            }
        }

        SensorStateAvro newSensorState = createSensorState(event, eventTimestamp);
        sensorsStates.put(event.getId(), newSensorState);

        log.info("Обновлен снимок для hubId: {}: {}", event.getHubId(), sensorsStates);

        return SensorsSnapshotAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(eventTimestamp)
                .setSensorsState(sensorsStates)
                .build();
    }

    private SensorStateAvro createSensorState(SensorEventAvro event, Instant eventTimestamp) {
        log.info("Создание состояния сенсора для события: {}, с меткой времени: {}", event, eventTimestamp);

        SensorStateAvro sensorState = SensorStateAvro.newBuilder()
                .setTimestamp(eventTimestamp)
                .setData(event.getPayload())
                .build();

        log.info("Созданное состояние сенсора: {}", sensorState);

        return sensorState;
    }
}