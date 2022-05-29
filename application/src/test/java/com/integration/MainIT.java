package com.integration;

import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class MainIT {

    @Test
    void runJavaAgents() throws IOException {
        Process application = Runtime.getRuntime().exec("java -javaagent:../static-java-agent/target/static-java-agent-1.0.jar -jar ./target/application-SNAPSHOT-1.0.jar");

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(application.getInputStream()))) {
            Process dynamicAgentLoading =
                    Runtime.getRuntime().exec("java -jar ../dynamic-java-agent/loader-application/target/loader-application-1.0.jar");
            Awaitility.await().atMost(Duration.FIVE_SECONDS).until(() -> bufferedReader.lines()
                    .anyMatch(log -> log.contains("Java agent loaded")));
            Awaitility.await().atMost(Duration.FIVE_SECONDS).until(() -> bufferedReader.lines()
                    .anyMatch(log -> log.contains("Dynamic agent attached")));
            dynamicAgentLoading.destroy();
        } finally {
            application.destroy();
        }
    }
}
