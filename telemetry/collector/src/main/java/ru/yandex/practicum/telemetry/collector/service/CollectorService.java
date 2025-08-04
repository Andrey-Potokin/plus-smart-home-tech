package ru.yandex.practicum.telemetry.collector.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.telemetry.collector.mapper.HubMapper;
import ru.yandex.practicum.telemetry.collector.mapper.SensorMapper;
import ru.yandex.practicum.telemetry.collector.model.hubs.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.sensors.SensorEvent;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class CollectorService {

    KafkaTemplate<Void, SpecificRecordBase> kafkaTemplate;

    private static final String TELEMETRY_SENSORS_KAFKA_TOPIC = "telemetry.sensors.v1";
    private static final String TELEMETRY_HUBS_KAFKA_TOPIC = "telemetry.hubs.v1";

    public void send(SensorEvent sensorEvent) {
        log.info("Отправка события {} с id={} в Kafka", sensorEvent,
                sensorEvent.getId());

        kafkaTemplate.send(TELEMETRY_SENSORS_KAFKA_TOPIC, SensorMapper.toAvro(sensorEvent))
                .whenComplete((result, exception) ->
                        handleSendResult(result, exception, sensorEvent));
    }

    public void send(HubEvent hubEvent) {
        log.info("Отправка события {} в Kafka", hubEvent);

        kafkaTemplate.send(TELEMETRY_HUBS_KAFKA_TOPIC, HubMapper.toAvro(hubEvent))
                .whenComplete((result, exception) ->
                        handleSendResult(result, exception, hubEvent));
    }

    private <T> void handleSendResult(SendResult<Void, SpecificRecordBase> result, Throwable exception, T event) {
        if (exception == null) {
            log.info("Событие {} успешно отправлено в Kafka, смещение: {}",
                    event.getClass().getSimpleName(), result.getRecordMetadata().offset());
        } else {
            log.error("Не удалось отправить событие {}: {}",
                    event.getClass().getSimpleName(), exception.getMessage());
        }
    }
}