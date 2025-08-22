package ru.yandex.practicum.telemetry.analyzer.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.telemetry.analyzer.model.scenario.Scenario;

@UtilityClass
public class ScenarioMapper {

    public  static Scenario toScenario(String hubId, ScenarioAddedEventAvro avro) {
        return Scenario.builder()
                .hubId(hubId)
                .name(avro.getName())
                .build();
    }
}