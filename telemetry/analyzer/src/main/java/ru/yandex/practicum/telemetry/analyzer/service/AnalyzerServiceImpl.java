package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.analyzer.model.Action;
import ru.yandex.practicum.telemetry.analyzer.model.Condition;
import ru.yandex.practicum.telemetry.analyzer.model.Scenario;
import ru.yandex.practicum.telemetry.analyzer.model.Sensor;
import ru.yandex.practicum.telemetry.analyzer.repository.ActionRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ConditionRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.SensorRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnalyzerServiceImpl implements AnalyzerService {

    ActionRepository actionRepository;
    ConditionRepository conditionRepository;
    ScenarioRepository scenarioRepository;
    SensorRepository sensorRepository;

    @Override
    public void handleSnapshot(SensorsSnapshotAvro event) {
        // TODO дописать
    }

    @Override
    public void handleHubEvent(HubEventAvro event) {
        String hubId = event.getHubId();
        SpecificRecordBase payload = (SpecificRecordBase) event.getPayload();

        switch (payload) {
            case DeviceAddedEventAvro deviceAdded -> handleDeviceAdded(hubId, deviceAdded.getId());
            case DeviceRemovedEventAvro deviceRemoved -> handleDeviceRemoved(hubId, deviceRemoved.getId());
            case ScenarioAddedEventAvro scenarioAdded -> handleScenarioAdded(hubId, scenarioAdded);
            case ScenarioRemovedEventAvro scenarioRemoved -> handleScenarioRemoved(scenarioRemoved.getName());
            case null, default -> throw new IllegalArgumentException("Неизвестный тип события: " + payload);
        }
    }

    private void handleDeviceAdded(String hubId, String id) {
        log.info("Добавление нового устройства с id={}, к хабу: {}", id, hubId);
        Sensor sensor = Sensor.builder()
                .id(id)
                .hubId(hubId)
                .build();
        sensorRepository.save(sensor);
    }

    private void handleDeviceRemoved(String hubId, String id) {
        log.info("Удаление устройства с id={}, у хаба: {}", id, hubId);
        sensorRepository.deleteById(id);
    }

    private void handleScenarioAdded(String hubId, ScenarioAddedEventAvro scenarioAdded) {
        log.info("Добавление нового сценария: {}", scenarioAdded.getName());
        Scenario scenario = Scenario.builder()
                .hubId(hubId)
                .name(scenarioAdded.getName())
                .build();
        scenarioRepository.save(scenario);

        scenarioAdded.getConditions().forEach(conditionAvro -> {
            try {
                conditionRepository.save(createCondition(conditionAvro));
            } catch (Exception e) {
                log.error("Ошибка при сохранении условия: {}", e.getMessage());
            }
        });

        scenarioAdded.getActions().forEach(actionAvro -> {
            try {
                actionRepository.save(createAction(actionAvro));
            } catch (Exception e) {
                log.error("Ошибка при сохранении действия: {}", e.getMessage());
            }
        });
    }

    private Condition createCondition(ScenarioConditionAvro conditionAvro) {
        Condition.ConditionBuilder condition = Condition.builder()
                .type(conditionAvro.getType().toString())
                .operation(conditionAvro.getOperation().toString());

        Optional.ofNullable(conditionAvro.getValue()).ifPresent(value -> {
            if (value instanceof Integer) {
                condition.value((Integer) value);
            } else if (value instanceof Boolean) {
                condition.value((Boolean) value ? 1 : 0);
            }
        });

        return condition.build();
    }

    private Action createAction(DeviceActionAvro actionAvro) {
        return Action.builder()
                .type(actionAvro.getType().toString())
                .value(actionAvro.getValue())
                .build();
    }

    private void handleScenarioRemoved(String name) {
        log.info("Удаление сценария: {}", name);
        scenarioRepository.deleteByName(name);
    }
}