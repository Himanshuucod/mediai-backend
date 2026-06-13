package com.mediai.mediai_backend.controller;

import com.mediai.mediai_backend.config.JwtUtil;
import com.mediai.mediai_backend.model.DiagnosisHistory;
import com.mediai.mediai_backend.model.User;
import com.mediai.mediai_backend.repository.DiagnosisHistoryRepository;
import com.mediai.mediai_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class HistoryController {

    @Autowired
    private DiagnosisHistoryRepository diagnosisHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/history")
    public ResponseEntity<List<DiagnosisHistory>> getHistory(
            @RequestHeader(value = "Authorization", required = false) String token) {

        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            String email = jwtUtil.extractEmail(jwt);
            User user = userRepository.findByEmail(email).orElse(null);
            if (user != null) {
                List<DiagnosisHistory> history = diagnosisHistoryRepository.findByUserId(user.getId());
                return ResponseEntity.ok(history);
            }
        }

        return ResponseEntity.ok(List.of());
    }
}