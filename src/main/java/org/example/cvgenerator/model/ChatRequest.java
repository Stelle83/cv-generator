package org.example.cvgenerator.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request object for the CV Generator")
public class ChatRequest {

    @Schema(description = "AI personality to use",
            allowableValues = {"teacher", "manager", "consultant"},
            example = "consultant")
    private String personality;

    @Schema(description = "Your message or request",
            example = "Write me a cover letter for a software consultant role")
    private String message;

    @Schema(description = "Session ID to maintain conversation history",
            example = "user-123-abc")
    private  String sessionId;
}
