package ru.yandex.practicum.telemetry.collector.model.hubs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScenarioAddedEvent extends HubEvent {

    @NotBlank
    @Size(min = 3)
    String name;

    @NotEmpty
    List<ScenarioCondition> conditions;

    @NotEmpty
    List<DeviceAction> actions;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }

    public ScenarioAddedEventAvro toAvro() {
        ScenarioAddedEventAvro avro = new ScenarioAddedEventAvro();
        avro.setName(this.name);

        List<ScenarioConditionAvro> avroConditions = this.conditions.stream()
                .map(ScenarioCondition::toAvro)
                .collect(Collectors.toList());
        avro.setConditions(avroConditions);

        List<DeviceActionAvro> avroActions = this.actions.stream()
                .map(DeviceAction::toAvro)
                .collect(Collectors.toList());
        avro.setActions(avroActions);

        return avro;
    }
}