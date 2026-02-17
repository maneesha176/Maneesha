package com.interviewslot.repository;

import com.interviewslot.domain.model.InterviewerAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InterviewerAvailabilityRepository extends JpaRepository<InterviewerAvailability, Long> {
    List<InterviewerAvailability> findByUserIdAndDateBetween(Long userId, LocalDate start, LocalDate end);
    Optional<InterviewerAvailability> findByUserIdAndDate(Long userId, LocalDate date);
}
