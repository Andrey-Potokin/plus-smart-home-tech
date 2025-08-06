package ru.yandex.practicum.telemetry.collector.model.hubs;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceAction {

    String sensorId;
    String type;
    int value;

    public DeviceActionAvro toAvro() {
        DeviceActionAvro avro = new DeviceActionAvro();
        avro.setSensorId(this.sensorId);
        avro.setType(ActionTypeAvro.valueOf(this.type));
        avro.setValue(this.value);
        return avro;
    }
}