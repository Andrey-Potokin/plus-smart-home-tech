package ru.yandex.practicum.telemetry.collector.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionOperationProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.telemetry.collector.util.TimestampConverter;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class HubMapper {
    public static HubEventAvro toAvro(HubEventProto event) {
        HubEventAvro avro = new HubEventAvro();
        setCommonFields(event, avro);

        switch (event.getPayloadCase()) {
            case DEVICE_ADDED:
                avro.setPayload(createDeviceAddedPayload(event));
                break;

            case DEVICE_REMOVED:
                avro.setPayload(createDeviceRemovedPayload(event));
                break;

            case SCENARIO_ADDED:
                avro.setPayload(createScenarioAddedPayload(event));
                break;

            case SCENARIO_REMOVED:
                avro.setPayload(createScenarioRemovedPayload(event));
                break;

            default:
                throw new IllegalArgumentException("Неизвестный тип события: " + event);
        }

        return avro;
    }

    private static DeviceAddedEventAvro createDeviceAddedPayload(HubEventProto event) {
        return DeviceAddedEventAvro.newBuilder()
                .setId(event.getDeviceAddedOrBuilder().getId())
                .setType(mapDeviceType(event.getDeviceAddedOrBuilder().getType()))
                .build();
    }

    private static DeviceTypeAvro mapDeviceType(DeviceTypeProto protoType) {
        return switch (protoType) {
            case MOTION_SENSOR -> DeviceTypeAvro.MOTION_SENSOR;
            case TEMPERATURE_SENSOR -> DeviceTypeAvro.TEMPERATURE_SENSOR;
            case LIGHT_SENSOR -> DeviceTypeAvro.LIGHT_SENSOR;
            case CLIMATE_SENSOR -> DeviceTypeAvro.CLIMATE_SENSOR;
            case SWITCH_SENSOR -> DeviceTypeAvro.SWITCH_SENSOR;
            default -> throw new IllegalArgumentException("Unknown device type: " + protoType);
        };
    }

    private static DeviceRemovedEventAvro createDeviceRemovedPayload(HubEventProto event) {
        return DeviceRemovedEventAvro.newBuilder()
                .setId(event.getDeviceRemovedOrBuilder().getId())
                .build();
    }

    private static ScenarioAddedEventAvro createScenarioAddedPayload(HubEventProto proto) {
        String name = proto.getScenarioAddedOrBuilder().getName();
        List<ScenarioConditionAvro> conditions = proto.getScenarioAddedOrBuilder().getConditionList().stream()
                .map(HubMapper::toAvro)
                .collect(Collectors.toList());
        List<DeviceActionAvro> actions = proto.getScenarioAddedOrBuilder().getActionList().stream()
                .map(HubMapper::toAvro)
                .collect(Collectors.toList());

        return new ScenarioAddedEventAvro(name, conditions, actions);
    }

    private static ScenarioConditionAvro toAvro(ScenarioConditionProto proto) {
        if (proto == null) {
            return null;
        }

        String sensorId = proto.getSensorId();
        ConditionTypeAvro type = mapConditionType(proto.getType());
        ConditionOperationAvro operation = mapConditionOperation(proto.getOperation());

        Object value = null;
        if (proto.hasBoolValue()) {
            value = proto.getBoolValue();
        } else if (proto.hasIntValue()) {
            value = proto.getIntValue();
        }

        return new ScenarioConditionAvro(sensorId, type, operation, value);
    }

    private static DeviceActionAvro toAvro(DeviceActionProto proto) {
        if (proto == null) {
            return null;
        }

        DeviceActionAvro avro = new DeviceActionAvro();
        avro.setSensorId(proto.getSensorId());
        avro.setType(mapActionType(proto.getType()));

        if (proto.hasValue()) {
            avro.setValue(proto.getValue());
        }

        return avro;
    }

    private static ConditionTypeAvro mapConditionType(ConditionTypeProto protoType) {
        return switch (protoType) {
            case MOTION -> ConditionTypeAvro.MOTION;
            case LUMINOSITY -> ConditionTypeAvro.LUMINOSITY;
            case SWITCH -> ConditionTypeAvro.SWITCH;
            case TEMPERATURE -> ConditionTypeAvro.TEMPERATURE;
            case CO2LEVEL -> ConditionTypeAvro.CO2LEVEL;
            case HUMIDITY -> ConditionTypeAvro.HUMIDITY;
            default -> throw new IllegalArgumentException("Unknown condition type: " + protoType);
        };
    }

    private static ConditionOperationAvro mapConditionOperation(ConditionOperationProto protoOperation) {
        return switch (protoOperation) {
            case EQUALS -> ConditionOperationAvro.EQUALS;
            case GREATER_THAN -> ConditionOperationAvro.GREATER_THAN;
            case LOWER_THAN -> ConditionOperationAvro.LOWER_THAN;
            default -> throw new IllegalArgumentException("Unknown condition operation: " + protoOperation);
        };
    }

    private static ActionTypeAvro mapActionType(ActionTypeProto protoType) {
        return switch (protoType) {
            case ACTIVATE -> ActionTypeAvro.ACTIVATE;
            case DEACTIVATE -> ActionTypeAvro.DEACTIVATE;
            case INVERSE -> ActionTypeAvro.INVERSE;
            case SET_VALUE -> ActionTypeAvro.SET_VALUE;
            default -> throw new IllegalArgumentException("Unknown action type: " + protoType);
        };
    }

    private static ScenarioRemovedEventAvro createScenarioRemovedPayload(HubEventProto proto) {
        return ScenarioRemovedEventAvro.newBuilder()
                .setName(proto.getScenarioRemovedOrBuilder().getName())
                .build();
    }

    private static void setCommonFields(HubEventProto event, HubEventAvro avro) {
        avro.setHubId(event.getHubId());
        avro.setTimestamp(TimestampConverter.convertTimestampToLong(event.getTimestamp()));
    }
}