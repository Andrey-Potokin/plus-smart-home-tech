package ru.yandex.practicum.telemetry.collector.model.sensors;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;

@Getter
@Setter
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MotionSensorEvent extends SensorEvent {

    @NotNull
    int linkQuality;
    @NotNull
    boolean motion;
    @NotNull
    int voltage;

    @Override
    public SensorEventType getType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }

    public MotionSensorAvro toAvro() {
        MotionSensorAvro avro = new MotionSensorAvro();
        avro.setLinkQuality(this.linkQuality);
        avro.setMotion(this.motion);
        avro.setVoltage(this.voltage);
        return avro;
    }
}