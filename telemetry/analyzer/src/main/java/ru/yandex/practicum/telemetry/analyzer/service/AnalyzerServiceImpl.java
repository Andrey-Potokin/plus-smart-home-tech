package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.analyzer.mapper.ActionMapper;
import ru.yandex.practicum.telemetry.analyzer.mapper.ConditionMapper;
import ru.yandex.practicum.telemetry.analyzer.mapper.ScenarioMapper;
import ru.yandex.practicum.telemetry.analyzer.mapper.SensorMapper;
import ru.yandex.practicum.telemetry.analyzer.repository.ActionRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ConditionRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioActionRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioConditionRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.SensorRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnalyzerServiceImpl implements AnalyzerService {

    ActionRepository actionRepository;
    ConditionRepository conditionRepository;
    ScenarioConditionRepository scenarioConditionRepository;
    ScenarioActionRepository scenarioActionRepository;
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

        sensorRepository.save(SensorMapper.toSensor(hubId, id));
    }

    private void handleDeviceRemoved(String hubId, String id) {
        log.info("Удаление устройства с id={}, у хаба: {}", id, hubId);
        sensorRepository.deleteById(id);
    }

    private void handleScenarioAdded(String hubId, ScenarioAddedEventAvro scenarioAdded) {
        log.info("Добавление нового сценария: {}", scenarioAdded.getName());

        scenarioRepository.save(ScenarioMapper.toScenario(hubId, scenarioAdded));

        scenarioAdded.getConditions().forEach(conditionAvro -> {
            try {
                conditionRepository.save(ConditionMapper.toCondition(conditionAvro));
            } catch (Exception e) {
                log.error("Ошибка при сохранении условия: {}", e.getMessage());
            }
        });

        scenarioAdded.getActions().forEach(actionAvro -> {
            try {
                actionRepository.save(ActionMapper.toAction(actionAvro));
            } catch (Exception e) {
                log.error("Ошибка при сохранении действия: {}", e.getMessage());
            }
        });
    }

    private void handleScenarioRemoved(String name) {
        log.info("Удаление сценария: {}", name);
        scenarioRepository.deleteByName(name);
    }
}