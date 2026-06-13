package com.mediai.mediai_backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class DiagnosisRequest {

    @NotNull(message = "Age is required")
    @Min(value = 1, message = "Age must be at least 1")
    @Max(value = 120, message = "Age must be less than 120")
    private Integer age;

    @NotBlank(message = "Gender is required")
    private String gender;

    private Double weight;

    @NotEmpty(message = "At least one symptom is required")
    private List<String> symptoms;

    @NotBlank(message = "Duration is required")
    private String duration;

    private String allergies;
}