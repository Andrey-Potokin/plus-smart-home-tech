package ru.yandex.practicum.telemetry.analyzer.client;

import com.google.protobuf.Timestamp;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequestProto;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.telemetry.analyzer.model.scenario.ScenarioAction;

import java.time.Instant;

@Slf4j
@Component
@ConfigurationProperties("grpc.client")
public class HubRouterClient {

    @GrpcClient("hub-router")
    HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterService;

    public void handleDeviceAction(String hubId, String scenarioName, ScenarioAction action) {
        try {
            DeviceActionRequestProto request = createDeviceActionRequest(hubId, scenarioName, action);
            log.debug("Отправка действия '{}' в хаб '{}' с данными: {}", action, hubId, request);
            hubRouterService.handleDeviceAction(request);
        } catch (StatusRuntimeException e) {
            log.error("gRPC ошибка при отправке действия '{}' в хаб '{}': {}.", action, hubId, e.getStatus().getDescription(), e);
        } catch (Exception e) {
            log.error("Ошибка отправки действия '{}' в хаб '{}': {}.", action, hubId, e.getMessage(), e);
        }
    }

    private DeviceActionRequestProto createDeviceActionRequest(String hubId, String scenarioName, ScenarioAction action) {
        return DeviceActionRequestProto.newBuilder()
                .setHubId(hubId)
                .setScenarioName(scenarioName)
                .setAction(createDeviceActionProto(action))
                .setTimestamp(currentTimestamp())
                .build();
    }

    private DeviceActionProto createDeviceActionProto(ScenarioAction action) {
        DeviceActionProto.Builder actionProto = DeviceActionProto.newBuilder()
                .setSensorId(action.getId().getSensorId())
                .setType(ActionTypeProto.valueOf(action.getAction().getType().name()));

        Integer value = action.getAction().getValue();
        if (value != null) {
            actionProto.setValue(value);
        }

        return actionProto.build();
    }

    private Timestamp currentTimestamp() {
        Instant now = Instant.now();
        return Timestamp.newBuilder()
                .setSeconds(now.getEpochSecond())
                .setNanos(now.getNano())
                .build();
    }
}