package com.interviewslot.repository;

import com.interviewslot.domain.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByScheduleId(Long scheduleId);
    long countByScheduleId(Long scheduleId);
}
