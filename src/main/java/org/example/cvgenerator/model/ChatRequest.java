package org.example.cvgenerator.model;

import lombok.Data;

@Data
public class ChatRequest {
    private String personality;
    private String message;
    private  String sessionId;
}
