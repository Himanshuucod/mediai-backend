package com.mediai.mediai_backend.service;

import com.mediai.mediai_backend.dto.DiagnosisRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromptBuilderService {

    public String buildPrompt(DiagnosisRequest request) {

        String symptoms = String.join(", ", request.getSymptoms());

        String prompt = """
                You are an experienced medical doctor with expertise in general medicine.
                A patient has come to you with the following details:
                
                Patient Information:
                - Age: %d years old
                - Gender: %s
                - Weight: %s kg
                - Symptoms: %s
                - Duration of symptoms: %s
                - Known allergies: %s
                
                Based on this information, provide a medical analysis in the following EXACT JSON format.
                Return ONLY the JSON, no extra text, no markdown, no backticks:
                
                {
                  "conditions": [
                    {
                      "name": "Condition name",
                      "probability": 85,
                      "severity": "Low/Medium/High",
                      "description": "Brief description of the condition"
                    }
                  ],
                  "medicines": [
                    "Medicine name and dosage"
                  ],
                  "homeRemedies": [
                    "Home remedy description"
                  ],
                  "warnings": [
                    "Warning message"
                  ],
                  "disclaimer": "This is an AI-generated result. Please consult a qualified doctor for proper diagnosis and treatment."
                }
                
                Rules:
                1. List maximum 3 most likely conditions
                2. Order conditions by probability (highest first)
                3. Probability must be a number between 1-100
                4. Severity must be exactly "Low", "Medium", or "High"
                5. Consider the patient's allergies when suggesting medicines
                6. If symptoms suggest emergency, set severity as "High" and add urgent warning
                7. Return ONLY valid JSON, nothing else
                """.formatted(
                request.getAge(),
                request.getGender(),
                request.getWeight() != null ? request.getWeight().toString() : "Not provided",
                symptoms,
                request.getDuration(),
                request.getAllergies() != null ? request.getAllergies() : "None"
        );

        return prompt;
    }
}
