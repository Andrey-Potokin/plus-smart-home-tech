package ru.yandex.practicum.telemetry.analyzer.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.telemetry.analyzer.model.action.Action;
import ru.yandex.practicum.telemetry.analyzer.model.action.ActionType;

import static ru.yandex.practicum.telemetry.analyzer.model.action.ActionType.ACTIVATE;
import static ru.yandex.practicum.telemetry.analyzer.model.action.ActionType.DEACTIVATE;
import static ru.yandex.practicum.telemetry.analyzer.model.action.ActionType.INVERSE;
import static ru.yandex.practicum.telemetry.analyzer.model.action.ActionType.SET_VALUE;

@UtilityClass
public class ActionMapper {


    public static Action toAction(DeviceActionAvro actionAvro) {
        return Action.builder()
                .type(toActionType(actionAvro.getType()))
                .value(actionAvro.getValue())
                .build();
    }

    private ActionType toActionType(ActionTypeAvro typeAvro) {
        return switch (typeAvro) {
            case ACTIVATE -> ACTIVATE;
            case DEACTIVATE -> DEACTIVATE;
            case INVERSE -> INVERSE;
            case SET_VALUE -> SET_VALUE;
        };
    }
}