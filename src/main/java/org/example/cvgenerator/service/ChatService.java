package org.example.cvgenerator.service;

import org.example.cvgenerator.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    @Value("${openrouter.api.key}")
    private String apiKey;

    @Value("${openrouter.api.url}")
    private String apiUrl;

    @Value("${openrouter.api.model}")
    private String model;

    private final RestClient restClient;

    public ChatService() {
        this.restClient = RestClient.create();
    }

    public ChatResponse chat(ChatRequest request) {
        String systemPrompt = getSystemPrompt(request.getPersonality());

        List<Message> messages = List.of(
                new Message("system", systemPrompt),
                new Message("user", request.getMessage())
        );

        OpenRouterRequest openRouterRequest = new OpenRouterRequest(model, messages);

        Map response = restClient.post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(openRouterRequest)
                .retrieve()
                .body(Map.class);

        String aiMessage = extractMessage(response);
        return new ChatResponse(aiMessage, request.getSessionId());
    }

    private String extractMessage(Map response) {
        var choices = (List<Map>) response.get("choices");
        var firstChoice = choices.get(0);
        var message = (Map) firstChoice.get("message");
        return (String) message.get("content");
    }

    private String getSystemPrompt(String personality) {
        return switch (personality) {
            case "teacher" -> """
                You are a career coach helping write cover letters for teaching positions.
                The applicant has many years of teaching experience and previously worked in consulting.
                Highlight: curriculum design, classroom management, student mentoring, communication skills.
                Translate consulting experience as an asset — business understanding sets them apart from other teachers.
                """;
            case "manager" -> """
                You are a career coach helping write cover letters for middle management positions.
                The applicant is a teacher transitioning to management.
                Translate teaching skills into management language:
                - Managing 30+ students daily = people management
                - Parent meetings = stakeholder communication
                - Lesson planning = project planning
                - Motivating students = team motivation
                - Selling ideas to students = persuasion and leadership
                """;
            case "consultant" -> """
                You are a career coach helping write cover letters for consulting positions.
                The applicant has previous consulting experience and has been teaching for many years.
                Teaching has strengthened: presentation skills, simplifying complex topics, managing groups,
                working under pressure, adapting communication style to different audiences.
                Frame the teaching period as intentional skill-building, not a career gap.
                """;
            default -> "You are a helpful assistant for writing cover letters.";
        };
    }
}