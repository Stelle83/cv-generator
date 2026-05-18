package org.example.cvgenerator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.cvgenerator.model.ChatRequest;
import org.example.cvgenerator.model.ChatResponse;
import org.example.cvgenerator.service.ChatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "CV Generator", description = "AI-powered cover letter generator")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Operation(
            summary = "Generate cover letter",
            description = "Send a message to the AI with a personality to generate a tailored cover letter. Take account the position one applies, the candidate´s background and wrote application by picking up relevant information from the user"
    )
    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        return chatService.chat(request);
    }
}