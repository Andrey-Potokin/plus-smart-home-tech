package ru.yandex.practicum.telemetry.collector.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.collector.model.hubs.DeviceAddedEvent;
import ru.yandex.practicum.telemetry.collector.model.hubs.DeviceRemovedEvent;
import ru.yandex.practicum.telemetry.collector.model.hubs.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.hubs.ScenarioAddedEvent;
import ru.yandex.practicum.telemetry.collector.model.hubs.ScenarioRemovedEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HubMapper {
    public static HubEventAvro toAvro(HubEvent event) {
        HubEventAvro avro = new HubEventAvro();
        setCommonFields(event, avro);

        switch (event) {
            case DeviceAddedEvent deviceAddedEvent -> avro.setPayload(deviceAddedEvent.toAvro());
            case DeviceRemovedEvent deviceRemovedEvent -> avro.setPayload(deviceRemovedEvent.toAvro());
            case ScenarioAddedEvent scenarioAddedEvent -> avro.setPayload(scenarioAddedEvent.toAvro());
            case ScenarioRemovedEvent scenarioRemovedEvent -> avro.setPayload(scenarioRemovedEvent.toAvro());
            default -> throw new IllegalArgumentException("Неизвестный тип события: " + event);
        }

        return avro;
    }

    private static void setCommonFields(HubEvent event, HubEventAvro avro) {
        avro.setHubId(event.getHubId());
        avro.setTimestamp(event.getTimestamp().toEpochMilli());
    }
}