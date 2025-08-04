package ru.yandex.practicum.telemetry.collector.model.sensors;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;

@Getter
@Setter
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LightSensorEvent extends SensorEvent {

    int linkQuality;
    int luminosity;

    @Override
    public SensorEventType getType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }

    public LightSensorAvro toAvro() {
        LightSensorAvro avro = new LightSensorAvro();
        avro.setLinkQuality(this.linkQuality);
        avro.setLuminosity(this.luminosity);
        return avro;
    }
}