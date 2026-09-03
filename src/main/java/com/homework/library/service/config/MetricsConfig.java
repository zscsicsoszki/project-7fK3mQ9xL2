package com.homework.library.service.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public Counter bookBorrowCounter(MeterRegistry meterRegistry) {
        return Counter.builder("books.borrow.total")
                .description("Total number of successful book borrow operations")
                .register(meterRegistry);
    }
}
