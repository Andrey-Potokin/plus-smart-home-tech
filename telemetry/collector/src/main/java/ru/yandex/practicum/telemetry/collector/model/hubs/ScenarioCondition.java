package ru.yandex.practicum.telemetry.collector.model.hubs;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScenarioCondition {

    String sensorId;
    String type;
    String operation;
    int value;

    public ScenarioConditionAvro toAvro() {
        ScenarioConditionAvro avro = new ScenarioConditionAvro();
        avro.setSensorId(this.sensorId);
        avro.setType(ConditionTypeAvro.valueOf(this.type));
        avro.setOperation(ConditionOperationAvro.valueOf(this.operation));
        avro.setValue(this.value);
        return avro;
    }
}