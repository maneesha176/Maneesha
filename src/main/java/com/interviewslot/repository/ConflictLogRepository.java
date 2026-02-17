package com.interviewslot.repository;

import com.interviewslot.domain.model.ConflictLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConflictLogRepository extends JpaRepository<ConflictLog, Long> {
}
