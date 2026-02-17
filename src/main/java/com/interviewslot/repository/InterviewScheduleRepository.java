package com.interviewslot.repository;

import com.interviewslot.domain.model.InterviewSchedule;
import com.interviewslot.domain.enums.ScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface InterviewScheduleRepository extends JpaRepository<InterviewSchedule, Long> {
    List<InterviewSchedule> findByCandidateId(Long candidateId);
    List<InterviewSchedule> findByStatus(ScheduleStatus status);
    List<InterviewSchedule> findByScheduledStartTimeBetween(LocalDateTime start, LocalDateTime end);
    
    @Query(value = "SELECT COUNT(*) FROM schedule_interviewers si " +
                   "JOIN interview_schedules isched ON si.schedule_id = isched.id " +
                   "WHERE si.user_id = :userId AND isched.scheduled_start_time BETWEEN :start AND :end", 
           nativeQuery = true)
    long countInterviewsForUserInRange(Long userId, LocalDateTime start, LocalDateTime end);
}
