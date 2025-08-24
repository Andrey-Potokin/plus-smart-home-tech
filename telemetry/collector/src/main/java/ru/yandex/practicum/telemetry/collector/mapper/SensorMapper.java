package ru.yandex.practicum.telemetry.collector.mapper;

import com.google.protobuf.Timestamp;
import lombok.experimental.UtilityClass;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

import java.time.Instant;

@UtilityClass
public class SensorMapper {
    public static SensorEventAvro toAvro(SensorEventProto event) {
        SensorEventAvro avro = new SensorEventAvro();
        setCommonFields(event, avro);

        switch (event.getPayloadCase()) {
            case MOTION_SENSOR:
                avro.setPayload(createMotionSensorPayload(event));
                break;

            case TEMPERATURE_SENSOR:
                avro.setPayload(createTemperatureSensorPayload(event));
                break;

            case LIGHT_SENSOR:
                avro.setPayload(createLightSensorPayload(event));
                break;

            case CLIMATE_SENSOR:
                avro.setPayload(createClimateSensorPayload(event));
                break;

            case SWITCH_SENSOR:
                avro.setPayload(createSwitchSensorPayload(event));
                break;

            default:
                throw new IllegalArgumentException("Неизвестный тип payload: " + event.getPayloadCase());
        }

        return avro;
    }

    private static MotionSensorAvro createMotionSensorPayload(SensorEventProto event) {
        return MotionSensorAvro.newBuilder()
                .setLinkQuality(event.getMotionSensorOrBuilder().getLinkQuality())
                .setMotion(event.getMotionSensorOrBuilder().getMotion())
                .setVoltage(event.getMotionSensorOrBuilder().getVoltage())
                .build();
    }

    private static TemperatureSensorAvro createTemperatureSensorPayload(SensorEventProto event) {
        return TemperatureSensorAvro.newBuilder()
                .setTemperatureC(event.getTemperatureSensorOrBuilder().getTemperatureC())
                .setTemperatureF(event.getTemperatureSensorOrBuilder().getTemperatureF())
                .build();
    }

    private static LightSensorAvro createLightSensorPayload(SensorEventProto event) {
        return LightSensorAvro.newBuilder()
                .setLinkQuality(event.getLightSensorOrBuilder().getLinkQuality())
                .setLuminosity(event.getLightSensorOrBuilder().getLuminosity())
                .build();
    }

    private static ClimateSensorAvro createClimateSensorPayload(SensorEventProto event) {
        return ClimateSensorAvro.newBuilder()
                .setTemperatureC(event.getClimateSensorOrBuilder().getTemperatureC())
                .setHumidity(event.getClimateSensorOrBuilder().getHumidity())
                .setCo2Level(event.getClimateSensorOrBuilder().getCo2Level())
                .build();
    }

    private static SwitchSensorAvro createSwitchSensorPayload(SensorEventProto event) {
        return SwitchSensorAvro.newBuilder()
                .setState(event.getSwitchSensorOrBuilder().getState())
                .build();
    }

    private static void setCommonFields(SensorEventProto event, SensorEventAvro avro) {
        avro.setId(event.getId());
        avro.setHubId(event.getHubId());

        Timestamp protoTimestamp = event.getTimestamp();
        Instant timestampInstant = Instant.ofEpochSecond(protoTimestamp.getSeconds(), protoTimestamp.getNanos());

        avro.setTimestamp(timestampInstant);
    }
}