package ru.yandex.practicum.telemetry.collector.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.telemetry.collector.model.hubs.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.sensors.SensorEvent;
import ru.yandex.practicum.telemetry.collector.service.CollectorService;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/events")
public class CollectorController {

    CollectorService service;

    @PostMapping("/sensors")
    @ResponseStatus(HttpStatus.OK)
    public void collectSensorEvent(@Valid @RequestBody SensorEvent sensorEvent) {
        log.info("Получено событие от датчика: {}", sensorEvent);
        service.send(sensorEvent);
    }

    @PostMapping("/hubs")
    @ResponseStatus(HttpStatus.OK)
    public void collectHubEvent(@Valid @RequestBody HubEvent hubEvent) {
        log.info("Получено событие от хаба: {}", hubEvent);
        service.send(hubEvent);
    }
}