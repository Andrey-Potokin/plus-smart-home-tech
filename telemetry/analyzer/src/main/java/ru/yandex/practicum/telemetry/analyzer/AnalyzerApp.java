package ru.yandex.practicum.telemetry.analyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class AnalyzerApp {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(AnalyzerApp.class, args);
        AnalyzerStarter aggregator = context.getBean(AnalyzerStarter.class);
        aggregator.run();
    }
}