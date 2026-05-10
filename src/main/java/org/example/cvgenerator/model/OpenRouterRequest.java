package org.example.cvgenerator.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OpenRouterRequest {
    private String model;
    private List<Message> messages;
}
