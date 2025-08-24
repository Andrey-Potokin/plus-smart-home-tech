package ru.yandex.practicum.kafka.serializer.deserializer;

import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public class SensorSnapshotAvroDeserializer extends BaseAvroDeserializer<SensorsSnapshotAvro> {
    public SensorSnapshotAvroDeserializer() {
        super(SensorsSnapshotAvro.getClassSchema());
    }
}