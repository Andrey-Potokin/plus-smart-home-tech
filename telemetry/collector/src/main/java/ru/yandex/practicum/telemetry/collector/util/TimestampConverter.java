package ru.yandex.practicum.telemetry.collector.util;

import com.google.protobuf.Timestamp;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TimestampConverter {
    public static long convertTimestampToLong(Timestamp timestamp) {
        long seconds = timestamp.getSeconds();
        int nanos = timestamp.getNanos();

        return seconds * 1000 + nanos / 1000000;
    }
}