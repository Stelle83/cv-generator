package org.example.cvgenerator.controller;

import org.example.cvgenerator.model.ChatRequest;
import org.example.cvgenerator.model.ChatResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ChatController {

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request){
        return new ChatResponse("Hello from CV Generator!", request.getSessionId());
    }
}
