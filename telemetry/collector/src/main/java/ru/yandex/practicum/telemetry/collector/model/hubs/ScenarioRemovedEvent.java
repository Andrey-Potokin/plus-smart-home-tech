package ru.yandex.practicum.telemetry.collector.model.hubs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

@Getter
@Setter
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScenarioRemovedEvent extends HubEvent {

    @NotBlank
    @Size(min = 3)
    String name;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }

    public ScenarioRemovedEventAvro toAvro() {
        ScenarioRemovedEventAvro avro = new ScenarioRemovedEventAvro();
        avro.setName(this.name);
        return avro;
    }
}