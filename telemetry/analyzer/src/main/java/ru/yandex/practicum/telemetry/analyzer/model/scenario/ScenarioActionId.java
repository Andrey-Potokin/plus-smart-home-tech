package ru.yandex.practicum.telemetry.analyzer.model.scenario;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Embeddable
@Getter @Setter @EqualsAndHashCode @ToString
@NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScenarioActionId implements Serializable {

    @NotNull
    Long scenarioId;

    @NotBlank
    String sensorId;

    @NotNull
    Long actionId;
}