package ru.yandex.practicum.telemetry.aggregator.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public interface AggregatorClient {

    Consumer<Void, SensorEventAvro> getConsumer();

    Producer<Void, SensorsSnapshotAvro> getProducer();

    void stop();
}