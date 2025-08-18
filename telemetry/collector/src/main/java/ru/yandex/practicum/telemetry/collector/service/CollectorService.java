package ru.yandex.practicum.telemetry.collector.service;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.collector.mapper.HubMapper;
import ru.yandex.practicum.telemetry.collector.mapper.SensorMapper;

@Slf4j
@GrpcService
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CollectorService extends CollectorControllerGrpc.CollectorControllerImplBase {

    KafkaTemplate<Void, SpecificRecordBase> producer;

    private static final String TELEMETRY_SENSORS_KAFKA_TOPIC = "telemetry.sensors.v1";
    private static final String TELEMETRY_HUBS_KAFKA_TOPIC = "telemetry.hubs.v1";

    @Override
    public void collectSensorEvent(SensorEventProto sensorEvent, StreamObserver<Empty> responseObserver) {
        try {
            log.info("Отправка события {} с id={} в Kafka", sensorEvent,
                    sensorEvent.getId());

            producer.send(TELEMETRY_SENSORS_KAFKA_TOPIC, SensorMapper.toAvro(sensorEvent))
                    .whenComplete((result, exception) ->
                            handleSendResult(result, exception, sensorEvent));

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }

    @Override
    public void collectHubEvent(HubEventProto hubEvent, StreamObserver<Empty> responseObserver) {
        try {
            log.info("Отправка события {} в Kafka", hubEvent);

            producer.send(TELEMETRY_HUBS_KAFKA_TOPIC, HubMapper.toAvro(hubEvent))
                    .whenComplete((result, exception) ->
                            handleSendResult(result, exception, hubEvent));

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
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