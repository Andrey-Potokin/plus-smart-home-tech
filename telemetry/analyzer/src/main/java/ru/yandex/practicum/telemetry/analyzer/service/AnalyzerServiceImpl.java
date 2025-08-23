package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.telemetry.analyzer.client.HubRouterClient;
import ru.yandex.practicum.telemetry.analyzer.mapper.ActionMapper;
import ru.yandex.practicum.telemetry.analyzer.mapper.ConditionMapper;
import ru.yandex.practicum.telemetry.analyzer.mapper.ScenarioMapper;
import ru.yandex.practicum.telemetry.analyzer.mapper.SensorMapper;
import ru.yandex.practicum.telemetry.analyzer.model.action.Action;
import ru.yandex.practicum.telemetry.analyzer.model.condition.Condition;
import ru.yandex.practicum.telemetry.analyzer.model.condition.ConditionOperation;
import ru.yandex.practicum.telemetry.analyzer.model.condition.ConditionType;
import ru.yandex.practicum.telemetry.analyzer.model.scenario.Scenario;
import ru.yandex.practicum.telemetry.analyzer.model.scenario.ScenarioAction;
import ru.yandex.practicum.telemetry.analyzer.model.scenario.ScenarioActionId;
import ru.yandex.practicum.telemetry.analyzer.model.scenario.ScenarioCondition;
import ru.yandex.practicum.telemetry.analyzer.model.scenario.ScenarioConditionId;
import ru.yandex.practicum.telemetry.analyzer.model.sensor.Sensor;
import ru.yandex.practicum.telemetry.analyzer.repository.ActionRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ConditionRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioActionRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioConditionRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.SensorRepository;

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
    HubRouterClient hubRouterClient;

    @Override
    public void handleSnapshot(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();
        log.info("Начало обработки Snapshot: {}. Хаб {}.", snapshot, hubId);

        Map<String, SensorStateAvro> sensorsState = snapshot.getSensorsState();
        log.debug("Состояние сенсоров для хаба '{}': {}", hubId, sensorsState);

        scenarioRepository.findByHubId(hubId).stream()
                .peek(scenario -> log.debug("Начата сверка сценария: {}", scenario.getName()))
                .forEach(scenario -> {
                    log.debug("Проверка условий сценария '{}'", scenario.getName());
                    boolean allConditionsOk = scenario.getConditions().stream()
                            .allMatch(condition -> {
                                boolean conditionResult = checkCondition(condition, sensorsState);
                                log.debug("Условие '{}' для сценария '{}' выполнено: {}", condition, scenario.getName(), conditionResult);
                                return conditionResult;
                            });

                    if (allConditionsOk) {
                        log.info("Все условия сценария '{}' выполнены. Выполнение действий.", scenario.getName());
                        scenario.getActions().forEach(action -> {
                            log.debug("Отправка действия '{}' для сценария '{}' в хаб '{}'", action, scenario.getName(), hubId);
                            hubRouterClient.handleDeviceAction(hubId, scenario.getName(), action);
                        });
                    } else {
                        log.info("Условия сценария '{}' не выполнены.", scenario.getName());
                    }
                });

        log.info("Обработка Snapshot для хаба '{}' завершена.", hubId);
    }

    private boolean checkCondition(ScenarioCondition scenarioCondition, Map<String, SensorStateAvro> sensorsState) {
        String sensorId = scenarioCondition.getSensor().getId();
        SensorStateAvro sensorState = sensorsState.get(sensorId);

        if (sensorState == null) {
            log.warn("Нет состояния для датчика: {}, пропускаем условие", sensorId);
            return false;
        }

        int actual = extractValue(sensorState, scenarioCondition.getCondition());
        Condition condition = scenarioCondition.getCondition();
        return evaluate(condition.getOperation(), actual, condition.getValue());
    }

    private int extractValue(SensorStateAvro state, Condition condition) {
        Object data = state.getData();
        ConditionType type = condition.getType();

        return switch (data) {
            case ClimateSensorAvro climateSensorAvro -> extractFromClimateSensor(climateSensorAvro, type);
            case TemperatureSensorAvro temperatureSensorAvro -> extractFromTemperatureSensor(temperatureSensorAvro, type);
            case LightSensorAvro lightSensorAvro -> extractFromLightSensor(lightSensorAvro, type);
            case MotionSensorAvro motionSensorAvro -> extractFromMotionSensor(motionSensorAvro, type);
            case SwitchSensorAvro switchSensorAvro -> extractFromSwitchSensor(switchSensorAvro, type);
            default -> throw new IllegalArgumentException("Неизвестный тип данных сенсора: " + data.getClass().getSimpleName());
        };
    }

    private int extractFromClimateSensor(ClimateSensorAvro sensor, ConditionType type) {
        return switch (type) {
            case TEMPERATURE -> sensor.getTemperatureC();
            case HUMIDITY -> sensor.getHumidity();
            case CO2LEVEL -> sensor.getCo2Level();
            default -> logAndReturn(type, "ClimateSensorAvro");
        };
    }

    private int extractFromTemperatureSensor(TemperatureSensorAvro sensor, ConditionType type) {
        if (type == ConditionType.TEMPERATURE) {
            return sensor.getTemperatureC();
        } else {
            return logAndReturn(type, "TemperatureSensorAvro");
        }
    }

    private int extractFromLightSensor(LightSensorAvro sensor, ConditionType type) {
        if (type == ConditionType.LUMINOSITY) {
            return sensor.getLuminosity();
        } else {
            return logAndReturn(type, "LightSensorAvro");
        }
    }

    private int extractFromMotionSensor(MotionSensorAvro sensor, ConditionType type) {
        if (type == ConditionType.MOTION) {
            return sensor.getMotion() ? 1 : 0;
        } else {
            return logAndReturn(type, "MotionSensorAvro");
        }
    }

    private int extractFromSwitchSensor(SwitchSensorAvro sensor, ConditionType type) {
        if (type == ConditionType.SWITCH) {
            return sensor.getState() ? 1 : 0;
        } else {
            return logAndReturn(type, "SwitchSensorAvro");
        }
    }

    private int logAndReturn(ConditionType type, String sensorType) {
        log.warn("Тип условия: {} неприменим к {}", type, sensorType);
        return 0;
    }

    private boolean evaluate(ConditionOperation op, int actual, int target) {
        return switch (op) {
            case EQUALS -> actual == target;
            case GREATER_THAN -> actual > target;
            case LOWER_THAN -> actual < target;
        };
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
        log.info("Добавление нового сценария: {}.", scenarioAdded.getName());

        Scenario scenario = scenarioRepository.save(ScenarioMapper.toScenario(hubId, scenarioAdded));

        scenarioAdded.getConditions().forEach(conditionAvro -> {
            String sensorId = conditionAvro.getSensorId();

            Sensor sensor = sensorRepository.findById(sensorId)
                    .orElseGet(() -> {
                        Sensor s = Sensor.builder()
                                .id(sensorId)
                                .hubId(hubId)
                                .build();
                        return sensorRepository.save(s);
                    });

            Condition condition = conditionRepository.save(ConditionMapper.toCondition(conditionAvro));

            ScenarioCondition scenarioCondition = ScenarioCondition.builder()
                    .id(new ScenarioConditionId(scenario.getId(), sensorId, condition.getId()))
                    .scenario(scenario)
                    .sensor(sensor)
                    .condition(condition)
                    .build();
            scenarioConditionRepository.save(scenarioCondition);
        });
        log.info("Условие сохранено.");

        scenarioAdded.getActions().forEach(actionAvro -> {
            String sensorId = actionAvro.getSensorId();

            Sensor sensor = sensorRepository.findById(sensorId)
                    .orElseGet(() -> {
                        Sensor s = Sensor.builder()
                                .id(sensorId)
                                .hubId(hubId)
                                .build();
                        return sensorRepository.save(s);
                    });

            Action action = actionRepository.save(ActionMapper.toAction(actionAvro));

            ScenarioAction scenarioAction = ScenarioAction.builder()
                    .id(new ScenarioActionId(scenario.getId(), sensorId, action.getId()))
                    .scenario(scenario)
                    .sensor(sensor)
                    .action(action)
                    .build();
            scenarioActionRepository.save(scenarioAction);
        });
        log.info("Действие сохранено.");

        log.info("Новый сценарий добавлен.");
    }

    @Transactional
    private void handleScenarioRemoved(String hubId, String name) {
        log.info("Удаление сценария '{}' из хаба '{}'.", name, hubId);
        scenarioRepository.findByHubIdAndName(hubId, name).ifPresent(scenarioRepository::delete);
        log.info("Сценарий '{}' для хаба '{}' удалён.", name, hubId);
    }
}