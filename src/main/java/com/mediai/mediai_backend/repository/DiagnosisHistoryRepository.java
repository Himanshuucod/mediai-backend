package com.mediai.mediai_backend.repository;

import com.mediai.mediai_backend.model.DiagnosisHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DiagnosisHistoryRepository extends JpaRepository<DiagnosisHistory, Long> {
    List<DiagnosisHistory> findByUserId(Long userId);
}