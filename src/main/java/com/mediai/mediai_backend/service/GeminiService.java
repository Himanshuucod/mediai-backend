package com.mediai.mediai_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;
    private final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String callGemini(String prompt) {
        try {
            // Build request body
            String requestBody = """
                {
                    "contents": [
                        {
                            "parts": [
                                {
                                    "text": %s
                                }
                            ]
                        }
                    ],
                    "generationConfig": {
                        "temperature": 0.3,
                        "maxOutputTokens": 8192
                    }
                }
                """.formatted(objectMapper.writeValueAsString(prompt));

            // Build HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GEMINI_URL + "?key=" + apiKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // Send request
            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            System.out.println("=== GEMINI RAW RESPONSE ===");
            System.out.println(response.body());
            System.out.println("===========================");

            // Parse response
            JsonNode root = objectMapper.readTree(response.body());
            String generatedText = root
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            return generatedText;

        } catch (Exception e) {
            System.err.println("Gemini API error: " + e.getMessage());
            return null;
        }
    }
}