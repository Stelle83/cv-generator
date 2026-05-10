# CV Generator — AI-Powered Cover Letter Assistant

A Spring Boot middleware service that generates personalized cover letters
using AI. Built as part of the Web Services and Integrations course at IT-Högskolan.

## What it does

This application acts as a bridge between the user and a Large Language Model (LLM).
It tailors cover letters based on a chosen personality/target role, maintains
conversation history per session, and handles errors gracefully.

## Personalities

| Personality | Target Role |
|---|---|
| `teacher` | Teaching positions in fashion or technology education |
| `manager` | Middle management, team lead, operations manager |
| `consultant` | Software consultant, implementation specialist |

## How to use

### Request
POST /api/v1/chat

```json
{
    "personality": "consultant",
    "message": "Write me a cover letter for a software consultant role",
    "sessionId": "my-session-123"
}
```

### Response
```json
{
    "message": "AI generated cover letter...",
    "sessionId": "my-session-123"
}
```

## Features

- AI-powered cover letter generation via OpenRouter
- Session memory — refine your letter conversationally
- Three personalities targeting different job types
- Global exception handling with clean error messages
- Automatic retry with exponential backoff on rate limits
- Swagger UI documentation at `/swagger-ui.html`

## Setup

1. Clone the repository
2. Create a `.env` file in the project root:
3. Run the application
4. Visit `http://localhost:8080/swagger-ui.html`

## Tech Stack

- Java 26
- Spring Boot 4.0.6
- Spring RestClient
- Spring Retry
- springdoc-openapi (Swagger)
- OpenRouter API (Claude 3.5 Haiku)

## API Documentation

Available at `http://localhost:8080/swagger-ui.html` when running locally.