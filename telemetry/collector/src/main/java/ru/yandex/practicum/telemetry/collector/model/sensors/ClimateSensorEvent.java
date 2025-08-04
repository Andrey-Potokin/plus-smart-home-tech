package ru.yandex.practicum.telemetry.collector.model.sensors;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;

@Getter
@Setter
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClimateSensorEvent extends SensorEvent {

    @NotNull
    int temperatureC;
    @NotNull
    int humidity;
    @NotNull
    int co2Level;

    @Override
    public SensorEventType getType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }

    public ClimateSensorAvro toAvro() {
        ClimateSensorAvro avro = new ClimateSensorAvro();
        avro.setTemperatureC(this.temperatureC);
        avro.setHumidity(this.humidity);
        avro.setCo2Level(this.co2Level);
        return avro;
    }
}