package org.example.cvgenerator.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.example.cvgenerator.model.Message;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "OPENROUTER_API_KEY=fake-test-key",
        "openrouter.api.model=test-model"
})
class OpenRouterClientTest {

    static WireMockServer wireMockServer = new WireMockServer(
            WireMockConfiguration.wireMockConfig().port(9090)
    );

    @BeforeAll
    static void startWireMock() {
        wireMockServer.start();
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }

    @BeforeEach
    void resetWireMock() {
        wireMockServer.resetAll();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("openrouter.api.url", () -> "http://localhost:9090/chat");
    }

    @Autowired
    private OpenRouterClient openRouterClient;

    @Test
    void shouldRetry_whenServerReturns429() {
        // ARRANGE - WireMock returns 429 every time
        wireMockServer.stubFor(post(urlEqualTo("/chat"))
                .willReturn(aResponse()
                        .withStatus(429)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":{\"message\":\"Rate limited\",\"code\":429}}")));

        List<Message> messages = List.of(
                new Message("user", "test message")
        );

        // ACT & ASSERT - should throw after 3 retries
        assertThrows(Exception.class, () ->
                openRouterClient.sendMessage(messages)
        );

        // Verify WireMock was called exactly 3 times
        wireMockServer.verify(3, postRequestedFor(urlEqualTo("/chat")));
    }

    @Test
    void shouldSucceed_whenServerReturnsValidResponse() {
        // ARRANGE - WireMock returns valid response
        wireMockServer.stubFor(post(urlEqualTo("/chat"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                    {
                        "choices": [
                            {
                                "message": {
                                    "content": "Here is your cover letter!"
                                }
                            }
                        ]
                    }
                """)));

        List<Message> messages = List.of(
                new Message("user", "test message")
        );

        // ACT
        String result = openRouterClient.sendMessage(messages);

        // ASSERT
        assertEquals("Here is your cover letter!", result);
        wireMockServer.verify(1, postRequestedFor(urlEqualTo("/chat")));
    }
}