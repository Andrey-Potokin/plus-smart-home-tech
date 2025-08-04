package ru.yandex.practicum.telemetry.collector.model.hubs;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

@Getter
@Setter
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceAddedEvent extends HubEvent {

    @NotBlank
    String id;

    @NotBlank
    String deviceType;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }

    public DeviceAddedEventAvro toAvro() {
        DeviceAddedEventAvro avro = new DeviceAddedEventAvro();
        avro.setId(this.id);
        avro.setType(DeviceTypeAvro.valueOf(this.deviceType));
        return avro;
    }
}