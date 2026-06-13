package com.mediai.mediai_backend.controller;

import com.mediai.mediai_backend.dto.DiagnosisRequest;
import com.mediai.mediai_backend.dto.DiagnosisResponse;
import com.mediai.mediai_backend.service.DiagnosisService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class DiagnosisController {

    @Autowired
    private DiagnosisService diagnosisService;

    @PostMapping("/diagnose")
    public ResponseEntity<DiagnosisResponse> diagnose(
            @Valid @RequestBody DiagnosisRequest request,
            @RequestHeader(value = "Authorization", required = false) String token) {

        DiagnosisResponse response = diagnosisService.diagnose(request, token);
        return ResponseEntity.ok(response);
    }
}