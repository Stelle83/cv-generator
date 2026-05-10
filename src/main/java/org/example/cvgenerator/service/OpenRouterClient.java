package org.example.cvgenerator.service;

import org.example.cvgenerator.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import java.util.List;
import java.util.Map;

@Service
public class OpenRouterClient {

    private static final Logger log = LoggerFactory.getLogger(OpenRouterClient.class);

    @Value("${openrouter.api.key}")
    private String apiKey;

    @Value("${openrouter.api.url}")
    private String apiUrl;

    @Value("${openrouter.api.model}")
    private String model;

    private final RestClient restClient;

    public OpenRouterClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Retryable(
            retryFor = {
                    HttpClientErrorException.TooManyRequests.class,
                    HttpServerErrorException.ServiceUnavailable.class
            },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public String sendMessage(List<Message> messages) {
        log.info("Attempting OpenRouter API call...");

        OpenRouterRequest openRouterRequest = new OpenRouterRequest(model, messages);

        Map response = restClient.post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(openRouterRequest)
                .retrieve()
                .body(Map.class);

        return extractMessage(response);
    }

    @Recover
    public String recover(HttpClientErrorException ex, List<Message> messages) {
        log.warn("All retries exhausted - HttpClientError: {}", ex.getMessage());
        throw ex;
    }

    @Recover
    public String recover(HttpServerErrorException ex, List<Message> messages) {
        log.warn("All retries exhausted - HttpServerError: {}", ex.getMessage());
        throw ex;
    }

    private String extractMessage(Map response) {
        var choices = (List<Map>) response.get("choices");
        var firstChoice = choices.get(0);
        var message = (Map) firstChoice.get("message");
        return (String) message.get("content");
    }
}