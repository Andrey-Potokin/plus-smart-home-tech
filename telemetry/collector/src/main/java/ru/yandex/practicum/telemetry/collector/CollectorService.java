package ru.yandex.practicum.telemetry.collector;

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
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
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
            log.info("В Collector от Hub_router поступило сообщение от датчика: {}", sensorEvent);

            SensorEventAvro avro = SensorMapper.toAvro(sensorEvent);
            producer.send(TELEMETRY_SENSORS_KAFKA_TOPIC, avro)
                    .whenComplete((result, exception) ->
                            handleSendResult(result, exception, avro));

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
            log.info("В Collector от Hub_router поступило сообщение для хаба: {}", hubEvent);

            HubEventAvro avro = HubMapper.toAvro(hubEvent);
            producer.send(TELEMETRY_HUBS_KAFKA_TOPIC, avro)
                    .whenComplete((result, exception) ->
                            handleSendResult(result, exception, avro));

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
            log.info("Сообщение {} успешно отправлено в Kafka, смещение: {}",
                    event.getClass().getSimpleName(), result.getRecordMetadata().offset());
        } else {
            log.error("Не удалось отправить событие {}: {}",
                    event.getClass().getSimpleName(), exception.getMessage());
        }
    }
}