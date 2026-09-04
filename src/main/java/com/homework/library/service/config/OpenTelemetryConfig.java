package com.homework.library.service.config;

import io.opentelemetry.exporter.logging.LoggingSpanExporter;
import org.springframework.boot.micrometer.tracing.opentelemetry.autoconfigure.SpanExporters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenTelemetryConfig {

    @Bean
    public SpanExporters spanExporters() {
        return SpanExporters.of(new LoggingSpanExporter());
    }
}
