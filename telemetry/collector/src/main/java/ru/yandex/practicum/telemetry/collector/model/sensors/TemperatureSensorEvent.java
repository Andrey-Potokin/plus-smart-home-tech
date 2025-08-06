package ru.yandex.practicum.telemetry.collector.model.sensors;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

@Getter
@Setter
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TemperatureSensorEvent extends SensorEvent {

    @NotNull
    int temperatureC;
    @NotNull
    int temperatureF;

    @Override
    public SensorEventType getType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }

    public TemperatureSensorAvro toAvro() {
        TemperatureSensorAvro avro = new TemperatureSensorAvro();
        avro.setTemperatureC(this.temperatureC);
        avro.setTemperatureF(this.temperatureF);
        return avro;
    }
}