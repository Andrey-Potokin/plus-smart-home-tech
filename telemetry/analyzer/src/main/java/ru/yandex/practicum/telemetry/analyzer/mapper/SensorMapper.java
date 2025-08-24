package ru.yandex.practicum.telemetry.analyzer.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.telemetry.analyzer.model.sensor.Sensor;

@UtilityClass
public class SensorMapper {

    public static Sensor toSensor(String hubId, String id) {
        return Sensor.builder()
                .id(id)
                .hubId(hubId)
                .build();
    }
}