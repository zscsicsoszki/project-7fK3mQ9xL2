package com.homework.library.integration;

import com.homework.library.service.BookApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(
        classes = BookApplication.class,
        properties = "spring.sql.init.data-locations="
)
@AutoConfigureMockMvc
@Sql("/integration-test-data.sql")
public class BaseIntegration {
}
