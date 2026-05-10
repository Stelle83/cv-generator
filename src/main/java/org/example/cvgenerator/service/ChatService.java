package org.example.cvgenerator.service;

import org.example.cvgenerator.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
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

    //Memory: sessionId -> list of messages
    private final Map<String, List<Message>> sessionMemory = new HashMap<>();

    public ChatService() {
        this.restClient = RestClient.create();
    }

    public ChatResponse chat(ChatRequest request) {
        String systemPrompt = getSystemPrompt(request.getPersonality());

        //Get or create historyfor this session
        String sessionId = request.getSessionId() != null
                ? request.getSessionId()
                : "default";

        List<Message> history = sessionMemory.computeIfAbsent(
                sessionId, k -> new ArrayList<>()
        );

        //Add user message to history
        history.add(new Message("user", request.getMessage()));

        //Build full message list: system prompt + history
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("system", systemPrompt));
        messages.addAll(history);

        OpenRouterRequest openRouterRequest = new OpenRouterRequest(model, messages);

        Map response = restClient.post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(openRouterRequest)
                .retrieve()
                .body(Map.class);

        String aiMessage = extractMessage(response);

        //Add AI response to history too
        history.add(new Message("assistant", aiMessage));

        return new ChatResponse(aiMessage, sessionId);
    }

    private String extractMessage(Map response) {
        var choices = (List<Map>) response.get("choices");
        var firstChoice = choices.get(0);
        var message = (Map) firstChoice.get("message");
        return (String) message.get("content");
    }

    private String getSystemPrompt(String personality) {
        String background = """
        The applicant's name is Stelle Simonlatser.
        
        BACKGROUND:
        - 20+ years in the textile and fashion industry
        - Pattern maker by profession and passion
        - Worked at Estonian National Opera creating costumes for ballet and opera
        - 6 years at Lectra (fashion software company) as Software Specialist in Professional Services
          * Supported customers, pre-sales consulting, software implementation
          * Worked in both Estonia and Sweden — international experience
        - 8 years as a teacher: pattern making, industry software, CLO3D
        - Currently studying Java development at IT-Högskolan (2-year vocational program)
        - Runs freelance work and online courses alongside teaching
        - Native Estonian, works in Swedish and English
        
        KEY STRENGTHS:
        - Unique combination: deep technical expertise + teaching + software + consulting
        - Bridge between technology and real industry needs
        - International experience (Estonia, Sweden)
        - Both B2B (Lectra clients) and educational experience
        - Self-driven: started own business, freelances, builds online courses
        """;

        return switch (personality) {
            case "teacher" -> background + """
            
            TARGET: Teaching position in fashion, textile, or technology education.
            
            TONE: Highlight passion for education, student development, and practical industry knowledge.
            Emphasize: CLO3D expertise, pattern making mastery, keeping education connected to real industry.
            Frame Java studies as staying current with technology trends in fashion tech.
            """;

            case "manager" -> background + """
            
            TARGET: Middle management position — team lead, project manager, or operations manager.
            
            TONE: Translate teaching and consulting experience into management language.
            Emphasize:
            - Managing and motivating people daily (students = teams)
            - Lectra role = client management, stakeholder communication, pre-sales
            - Running own business = entrepreneurial mindset
            - International experience = adaptable, cross-cultural communication
            - Freelancing alongside teaching = self-management and prioritization
            Frame as: someone who has always led, coached, and delivered results.
            """;

            case "consultant" -> background + """
            
            TARGET: Consulting role in fashion tech, software implementation, or product development.
            
            TONE: This is a natural return to consulting — frame it as coming full circle.
            Emphasize:
            - Lectra experience = direct consulting background (pre-sales, implementation, customer support)
            - Teaching = ability to explain complex things simply to any audience
            - Pattern making + software = rare technical depth in a niche field
            - Java studies = investing in future tech capabilities
            Frame teaching period as: staying connected to industry needs, building training expertise,
            not a career gap but a strategic choice that deepened expertise.
            """;

            default -> background + "You are a helpful assistant for writing cover letters.";
        };
    }
}