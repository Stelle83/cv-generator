package org.example.cvgenerator.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatResponse {
    private String message;
    private String sessionId;
}
