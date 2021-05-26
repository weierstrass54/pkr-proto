package com.ckontur.pkr.testcontainers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startable;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.net.URI;
import java.util.Optional;

@Slf4j
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = SpringBootPostgreSQLContainerTests.SpringBootTestInitializer.class)
public abstract class SpringBootPostgreSQLContainerTests extends AbstractTestNGSpringContextTests {
    private static PostgreSQLContainer<?> postgreSQLContainer;

    @BeforeSuite
    protected static void beforeSuite() throws Exception {
        SpringConfig config = SpringConfig.of("application.yml");
        URI uri = URI.create(config.getSpring().getDataSource().getUrl().substring(5));
        if (!uri.getScheme().equals("postgresql")) {
            throw new Exception("Database source is invalid.");
        }
        postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withUsername(config.getSpring().getDataSource().getUsername())
            .withPassword(config.getSpring().getDataSource().getPassword())
            .withExposedPorts(uri.getPort())
            .withDatabaseName(uri.getPath());
        postgreSQLContainer.start();
        log.info("PostgreSQL container has been started.");
    }

    @AfterSuite
    protected static void afterSuite() {
        Optional.ofNullable(postgreSQLContainer).ifPresent(Startable::close);
        log.info("PostgreSQL container has been closed.");
    }

    static class SpringBootTestInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues.of("spring.datasource.url=" + postgreSQLContainer.getJdbcUrl())
                .applyTo(applicationContext.getEnvironment());
        }
    }
}
