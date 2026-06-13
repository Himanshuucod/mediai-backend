package com.mediai.mediai_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediai.mediai_backend.config.JwtUtil;
import com.mediai.mediai_backend.dto.DiagnosisRequest;
import com.mediai.mediai_backend.dto.DiagnosisResponse;
import com.mediai.mediai_backend.model.DiagnosisHistory;
import com.mediai.mediai_backend.model.User;
import com.mediai.mediai_backend.repository.DiagnosisHistoryRepository;
import com.mediai.mediai_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DiagnosisService {

    @Autowired
    private PromptBuilderService promptBuilderService;

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private DiagnosisHistoryRepository diagnosisHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public DiagnosisResponse diagnose(DiagnosisRequest request, String token) {
        DiagnosisResponse response = new DiagnosisResponse();

        try {
            // Step 1: Build prompt
            String prompt = promptBuilderService.buildPrompt(request);

            // Step 2: Call Gemini
            String geminiResponse = geminiService.callGemini(prompt);

            if (geminiResponse == null) {
                response.setSuccess(false);
                response.setMessage("Failed to get response from AI");
                return response;
            }

            // Step 3: Clean response
            String cleanedResponse = geminiResponse
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            // Step 4: Parse JSON response
            JsonNode root = objectMapper.readTree(cleanedResponse);

            // Parse conditions
            List<DiagnosisResponse.Condition> conditions = new ArrayList<>();
            for (JsonNode condNode : root.path("conditions")) {
                DiagnosisResponse.Condition condition = new DiagnosisResponse.Condition();
                condition.setName(condNode.path("name").asText());
                condition.setProbability(condNode.path("probability").asInt());
                condition.setSeverity(condNode.path("severity").asText());
                condition.setDescription(condNode.path("description").asText());
                conditions.add(condition);
            }

            // Parse medicines
            List<String> medicines = new ArrayList<>();
            for (JsonNode med : root.path("medicines")) {
                medicines.add(med.asText());
            }

            // Parse home remedies
            List<String> homeRemedies = new ArrayList<>();
            for (JsonNode remedy : root.path("homeRemedies")) {
                homeRemedies.add(remedy.asText());
            }

            // Parse warnings
            List<String> warnings = new ArrayList<>();
            for (JsonNode warning : root.path("warnings")) {
                warnings.add(warning.asText());
            }

            // Step 5: Build result
            DiagnosisResponse.DiagnosisResult result = new DiagnosisResponse.DiagnosisResult();
            result.setConditions(conditions);
            result.setMedicines(medicines);
            result.setHomeRemedies(homeRemedies);
            result.setWarnings(warnings);
            result.setDisclaimer(root.path("disclaimer").asText());

            // Step 6: Get user from token and save to database
            DiagnosisHistory history = new DiagnosisHistory();
            history.setSymptoms(String.join(", ", request.getSymptoms()));
            history.setDuration(request.getDuration());
            history.setAllergies(request.getAllergies());
            history.setAiResponse(cleanedResponse);

            if (token != null && token.startsWith("Bearer ")) {
                String jwt = token.substring(7);
                String email = jwtUtil.extractEmail(jwt);
                User user = userRepository.findByEmail(email).orElse(null);
                if (user != null) {
                    history.setUser(user);
                }
            }

            diagnosisHistoryRepository.save(history);
            System.out.println("✅ Diagnosis saved to database!");

            response.setSuccess(true);
            response.setMessage("Diagnosis completed successfully");
            response.setResult(result);

        } catch (Exception e) {
            System.err.println("Diagnosis error: " + e.getMessage());
            response.setSuccess(false);
            response.setMessage("Error processing diagnosis: " + e.getMessage());
        }

        return response;
    }
}