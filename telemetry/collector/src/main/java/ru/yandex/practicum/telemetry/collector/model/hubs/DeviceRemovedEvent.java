package ru.yandex.practicum.telemetry.collector.model.hubs;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;

@Getter
@Setter
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceRemovedEvent extends HubEvent {

    @NotBlank
    String id;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }

    public DeviceRemovedEventAvro toAvro() {
        DeviceRemovedEventAvro avro = new DeviceRemovedEventAvro();
        avro.setId(this.id);
        return avro;
    }
}