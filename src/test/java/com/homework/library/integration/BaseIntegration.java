package com.homework.library.integration;

import com.homework.library.service.BookApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

@SpringBootTest(
        classes = BookApplication.class,
        properties = "spring.sql.init.data-locations=classpath:db/integration-test-data.sql"
)
@AutoConfigureMockMvc
public class BaseIntegration {
}
