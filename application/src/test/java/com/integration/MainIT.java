package com.integration;

import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class MainIT {

    private static final Logger logger = LoggerFactory.getLogger(MainIT.class);

    @Test
    @EnabledOnOs(OS.LINUX)
    void runJavaAgents() throws IOException {
        logger.info("Started test");
        Process application = Runtime.getRuntime().exec("java -javaagent:../static-java-agent/target/static-java-agent-1.0.jar -jar ./target/application-SNAPSHOT-1.0.jar");
        Process dynamicAgentLoading = Runtime.getRuntime().exec("java -jar ../dynamic-java-agent/loader-application/target/loader-application-1.0.jar");
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(application.getInputStream()))) {
            Awaitility.await().atMost(Duration.FIVE_SECONDS).until(() -> bufferedReader.lines()
                    .anyMatch(log -> log.contains("Java agent loaded")));
            logger.info("Static agent loaded");
            Awaitility.await().atMost(Duration.FIVE_SECONDS).until(() -> bufferedReader.lines()
                    .anyMatch(log -> log.contains("Dynamic agent attached")));
            logger.info("Dynamic agent loaded");
        } finally {
            logger.info("Destroying processes");
            dynamicAgentLoading.destroy();
            application.destroy();
        }
    }
}
