package ru.yandex.practicum.telemetry.analyzer.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.telemetry.analyzer.model.condition.Condition;
import ru.yandex.practicum.telemetry.analyzer.model.condition.ConditionOperation;
import ru.yandex.practicum.telemetry.analyzer.model.condition.ConditionType;

import java.util.Optional;

import static ru.yandex.practicum.telemetry.analyzer.model.condition.ConditionOperation.EQUALS;
import static ru.yandex.practicum.telemetry.analyzer.model.condition.ConditionOperation.GREATER_THAN;
import static ru.yandex.practicum.telemetry.analyzer.model.condition.ConditionOperation.LOWER_THAN;
import static ru.yandex.practicum.telemetry.analyzer.model.condition.ConditionType.CO2LEVEL;
import static ru.yandex.practicum.telemetry.analyzer.model.condition.ConditionType.HUMIDITY;
import static ru.yandex.practicum.telemetry.analyzer.model.condition.ConditionType.LUMINOSITY;
import static ru.yandex.practicum.telemetry.analyzer.model.condition.ConditionType.MOTION;
import static ru.yandex.practicum.telemetry.analyzer.model.condition.ConditionType.SWITCH;
import static ru.yandex.practicum.telemetry.analyzer.model.condition.ConditionType.TEMPERATURE;

@UtilityClass
public class ConditionMapper {

    public static Condition toCondition(ScenarioConditionAvro conditionAvro) {
        Condition.ConditionBuilder condition = Condition.builder()
                .type(toConditionType(conditionAvro.getType()))
                .operation(toConditionOperation(conditionAvro.getOperation()));

        Optional.ofNullable(conditionAvro.getValue()).ifPresent(value -> {
            if (value instanceof Integer) {
                condition.value((Integer) value);
            } else if (value instanceof Boolean) {
                condition.value((Boolean) value ? 1 : 0);
            }
        });

        return condition.build();
    }

    private ConditionType toConditionType(ConditionTypeAvro typeAvro) {
        return switch (typeAvro) {
            case MOTION -> MOTION;
            case LUMINOSITY -> LUMINOSITY;
            case SWITCH -> SWITCH;
            case TEMPERATURE -> TEMPERATURE;
            case CO2LEVEL -> CO2LEVEL;
            case HUMIDITY -> HUMIDITY;
        };
    }

    private ConditionOperation toConditionOperation(ConditionOperationAvro operationAvro) {
        return switch (operationAvro) {
            case EQUALS -> EQUALS;
            case GREATER_THAN -> GREATER_THAN;
            case LOWER_THAN -> LOWER_THAN;
        };
    }
}