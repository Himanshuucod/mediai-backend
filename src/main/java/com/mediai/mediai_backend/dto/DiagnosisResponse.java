package com.mediai.mediai_backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class DiagnosisResponse {

    private boolean success;
    private String message;
    private DiagnosisResult result;

    @Data
    public static class DiagnosisResult {
        private List<Condition> conditions;
        private List<String> medicines;
        private List<String> homeRemedies;
        private List<String> warnings;
        private String disclaimer;
    }

    @Data
    public static class Condition {
        private String name;
        private Integer probability;
        private String severity;
        private String description;
    }
}