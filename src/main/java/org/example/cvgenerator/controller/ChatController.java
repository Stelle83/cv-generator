package org.example.cvgenerator.controller;

import org.example.cvgenerator.model.ChatRequest;
import org.example.cvgenerator.model.ChatResponse;
import org.example.cvgenerator.service.ChatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        return chatService.chat(request);
    }
}