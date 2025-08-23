package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.analyzer.mapper.ActionMapper;
import ru.yandex.practicum.telemetry.analyzer.mapper.ConditionMapper;
import ru.yandex.practicum.telemetry.analyzer.mapper.ScenarioMapper;
import ru.yandex.practicum.telemetry.analyzer.mapper.SensorMapper;
import ru.yandex.practicum.telemetry.analyzer.model.scenario.Scenario;
import ru.yandex.practicum.telemetry.analyzer.model.scenario.ScenarioAction;
import ru.yandex.practicum.telemetry.analyzer.model.scenario.ScenarioActionId;
import ru.yandex.practicum.telemetry.analyzer.model.scenario.ScenarioCondition;
import ru.yandex.practicum.telemetry.analyzer.model.scenario.ScenarioConditionId;
import ru.yandex.practicum.telemetry.analyzer.repository.ActionRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ConditionRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioActionRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioConditionRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.SensorRepository;

import java.util.List;
import java.util.Map;

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
    public void handleSnapshot(SensorsSnapshotAvro snapshotAvro) {
        log.info("Начало обработки Снапшота: {}.", snapshotAvro);
        String hubId = snapshotAvro.getHubId();
        // набор состояний датчиков
        Map<String, SensorStateAvro> sensorStateAvro = snapshotAvro.getSensorsState();
        // сценарии хаба
        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);
        for (Scenario scenario : scenarios) {
        }
    }

    @Override
    public void handleHubEvent(HubEventAvro event) {
        log.info("Начало обработки события {}.", event);
        String hubId = event.getHubId();
        SpecificRecordBase payload = (SpecificRecordBase) event.getPayload();

        switch (payload) {
            case DeviceAddedEventAvro deviceAdded -> handleDeviceAdded(hubId, deviceAdded.getId());
            case DeviceRemovedEventAvro deviceRemoved -> handleDeviceRemoved(hubId, deviceRemoved.getId());
            case ScenarioAddedEventAvro scenarioAdded -> handleScenarioAdded(hubId, scenarioAdded);
            case ScenarioRemovedEventAvro scenarioRemoved -> handleScenarioRemoved(hubId, scenarioRemoved.getName());
            case null, default -> throw new IllegalArgumentException("Неизвестный тип события: " + payload);
        }
    }

    @Transactional
    private void handleDeviceAdded(String hubId, String id) {
        log.info("Добавление нового устройства с id={}, в хаб: {}.", id, hubId);
        sensorRepository.save(SensorMapper.toSensor(hubId, id));
        log.info("Новое устройство добавлено.");
    }

    @Transactional
    private void handleDeviceRemoved(String hubId, String id) {
        log.info("Удаление устройства с id={}, из хаба: {}.", id, hubId);
        sensorRepository.deleteById(id);
        log.info("Устройство удалено.");
    }

    @Transactional
    private void handleScenarioAdded(String hubId, ScenarioAddedEventAvro scenarioAdded) {
        log.info("Добавление нового сценария: {}", scenarioAdded.getName());

        long scenarioId = scenarioRepository.save(ScenarioMapper.toScenario(hubId, scenarioAdded)).getId();

        scenarioAdded.getConditions().forEach(conditionAvro -> {
            String sensorId = conditionAvro.getSensorId();
            try {
                long conditionId = conditionRepository.save(ConditionMapper.toCondition(conditionAvro)).getId();
                ScenarioConditionId scenarioConditionId = createScenarioConditionId(scenarioId, sensorId, conditionId);
                ScenarioCondition scenarioCondition = new ScenarioCondition(scenarioConditionId);
                scenarioConditionRepository.save(scenarioCondition);
            } catch (Exception e) {
                log.error("Ошибка при сохранении условия: {}", e.getMessage());
            }
        });
        log.info("Условие сохранено.");

        scenarioAdded.getActions().forEach(actionAvro -> {
            String sensorId = actionAvro.getSensorId();
            try {
                long actionId = actionRepository.save(ActionMapper.toAction(actionAvro)).getId();
                ScenarioActionId scenarioActionId = createScenarioActionId(scenarioId, sensorId, actionId);
                ScenarioAction scenarioAction = new ScenarioAction(scenarioActionId);
                scenarioActionRepository.save(scenarioAction);
            } catch (Exception e) {
                log.error("Ошибка при сохранении действия: {}", e.getMessage());
            }
        });
        log.info("Действие сохранено.");
        log.info("Новый сценарий добавлен.");
    }

    private ScenarioConditionId createScenarioConditionId(long scenarioId, String sensorId, long conditionId) {
        return ScenarioConditionId.builder()
                .scenarioId(scenarioId)
                .sensorId(sensorId)
                .conditionId(conditionId)
                .build();
    }

    private ScenarioActionId createScenarioActionId(long scenarioId, String sensorId, long actionId) {
        return ScenarioActionId.builder()
                .scenarioId(scenarioId)
                .sensorId(sensorId)
                .actionId(actionId)
                .build();
    }

    @Transactional
    private void handleScenarioRemoved(String hubId, String name) {
        log.info("Удаление сценария '{}' из хаба '{}'", name, hubId);
        scenarioRepository.findByHubIdAndName(hubId, name).ifPresent(scenarioRepository::delete);
        log.info("Сценарий '{}' для хаба '{}' удалён.", name, hubId);
    }
}