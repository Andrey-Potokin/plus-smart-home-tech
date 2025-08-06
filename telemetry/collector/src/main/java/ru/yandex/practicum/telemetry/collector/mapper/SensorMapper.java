package ru.yandex.practicum.telemetry.collector.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.model.sensors.ClimateSensorEvent;
import ru.yandex.practicum.telemetry.collector.model.sensors.LightSensorEvent;
import ru.yandex.practicum.telemetry.collector.model.sensors.MotionSensorEvent;
import ru.yandex.practicum.telemetry.collector.model.sensors.SensorEvent;
import ru.yandex.practicum.telemetry.collector.model.sensors.SwitchSensorEvent;
import ru.yandex.practicum.telemetry.collector.model.sensors.TemperatureSensorEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SensorMapper {
    public static SensorEventAvro toAvro(SensorEvent event) {
        SensorEventAvro avro = new SensorEventAvro();
        setCommonFields(event, avro);

        switch (event) {
            case ClimateSensorEvent climateSensorEvent -> avro.setPayload(climateSensorEvent.toAvro());
            case LightSensorEvent lightSensorEvent -> avro.setPayload(lightSensorEvent.toAvro());
            case MotionSensorEvent motionSensorEvent -> avro.setPayload(motionSensorEvent.toAvro());
            case SwitchSensorEvent switchSensorEvent -> avro.setPayload(switchSensorEvent.toAvro());
            case TemperatureSensorEvent temperatureSensorEvent -> avro.setPayload(temperatureSensorEvent.toAvro());
            default -> throw new IllegalArgumentException("Неизвестный тип сенсора");
        }

        return avro;
    }

    private static void setCommonFields(SensorEvent event, SensorEventAvro avro) {
        avro.setId(event.getId());
        avro.setHubId(event.getHubId());
        avro.setTimestamp(event.getTimestamp().toEpochMilli());
    }
}