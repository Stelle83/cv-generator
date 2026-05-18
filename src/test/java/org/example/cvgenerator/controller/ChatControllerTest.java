package org.example.cvgenerator.controller;

import org.example.cvgenerator.model.ChatRequest;
import org.example.cvgenerator.model.ChatResponse;
import org.example.cvgenerator.service.ChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(properties = {
        "OPENROUTER_API_KEY=fake-test-key",
        "openrouter.api.url=http://localhost:8080",
        "openrouter.api.model=test-model"
})
@AutoConfigureMockMvc
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatService chatService;

    @Test
    void chat_shouldReturnResponse_whenValidRequest() throws Exception {
        // ARRANGE - set up what ChatService should return
        when(chatService.chat(any(ChatRequest.class)))
                .thenReturn(new ChatResponse("Here is your cover letter!", "session-123"));

        // ACT & ASSERT - send request and check response
        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "personality": "consultant",
                        "message": "Write me a cover letter",
                        "sessionId": "session-123"
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Here is your cover letter!"))
                .andExpect(jsonPath("$.sessionId").value("session-123"));
    }

    @Test
    void chat_shouldReturn200_whenSessionIdIsMissing() throws Exception {
        when(chatService.chat(any(ChatRequest.class)))
                .thenReturn(new ChatResponse("Here is your cover letter!", "default"));

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "personality": "consultant",
                        "message": "Write me a cover letter"
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("default"));
    }

    @Test
    void chat_shouldUseConsultantPersonality() throws Exception {
        when(chatService.chat(any(ChatRequest.class)))
                .thenReturn(new ChatResponse("Consultant cover letter!", "session-1"));

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "personality": "consultant",
                    "message": "Write me a cover letter",
                    "sessionId": "session-1"
                }
            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Consultant cover letter!"));
    }

    @Test
    void chat_shouldUseManagerPersonality() throws Exception {
        when(chatService.chat(any(ChatRequest.class)))
                .thenReturn(new ChatResponse("Manager cover letter!", "session-2"));

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "personality": "manager",
                    "message": "Write me a cover letter",
                    "sessionId": "session-2"
                }
            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Manager cover letter!"));
    }

    @Test
    void chat_shouldUseTeacherPersonality() throws Exception {
        when(chatService.chat(any(ChatRequest.class)))
                .thenReturn(new ChatResponse("Teacher cover letter!", "session-3"));

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "personality": "teacher",
                    "message": "Write me a cover letter",
                    "sessionId": "session-3"
                }
            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Teacher cover letter!"));
    }

    @Test
    void chat_shouldUseDefaultPersonality_whenUnknownPersonality() throws Exception {
        when(chatService.chat(any(ChatRequest.class)))
                .thenReturn(new ChatResponse("Default cover letter!", "session-4"));

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "personality": "unknown",
                    "message": "Write me a cover letter",
                    "sessionId": "session-4"
                }
            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Default cover letter!"));
    }
}