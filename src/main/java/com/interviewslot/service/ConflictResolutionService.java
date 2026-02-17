package com.interviewslot.service;

import com.interviewslot.domain.enums.ConflictType;
import com.interviewslot.domain.enums.ScheduleStatus;
import com.interviewslot.domain.enums.UserRole;
import com.interviewslot.domain.model.*;
import com.interviewslot.repository.ConflictLogRepository;
import com.interviewslot.repository.InterviewRoundRepository;
import com.interviewslot.repository.InterviewScheduleRepository;
import com.interviewslot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConflictResolutionService {

    private final InterviewScheduleRepository scheduleRepository;
    private final InterviewRoundRepository roundRepository;
    private final UserRepository userRepository;
    private final AvailabilityService availabilityService;
    private final LoadBalancingService loadBalancingService;
    private final NotificationService notificationService;
    private final ConflictLogRepository conflictLogRepository;

    @Transactional
    public void handleInterviewerCancellation(Long scheduleId, Long cancelledInterviewerId) {
        InterviewSchedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();

        long hoursUntilInterview = ChronoUnit.HOURS.between(LocalDateTime.now(), schedule.getScheduledStartTime());

        if (hoursUntilInterview < 24) {
            Optional<User> replacement = findReplacement(schedule, cancelledInterviewerId);

            if (replacement.isPresent()) {
                schedule.replaceInterviewer(cancelledInterviewerId, replacement.get().getId());
                scheduleRepository.save(schedule);

                logConflict(scheduleId, ConflictType.INTERVIEWER_CANCELLED, "Auto-replaced with " + replacement.get().getName());
                notificationService.notifyInterviewerReplacement(schedule, replacement.get());
            } else {
                notificationService.alertHRForManualReschedule(schedule);
                schedule.setStatus(ScheduleStatus.PENDING_RESCHEDULE);
                scheduleRepository.save(schedule);
                logConflict(scheduleId, ConflictType.INTERVIEWER_CANCELLED, "No replacement found - escalated to HR");
            }
        }
    }

    private Optional<User> findReplacement(InterviewSchedule schedule, Long cancelledId) {
        InterviewRound round = roundRepository.findByPipelineIdAndRoundNumber(schedule.getPipelineId(), schedule.getRoundNumber()).orElseThrow();

        List<User> candidates = userRepository.findByCompanyIdAndRoleAndSkillsIn(
            schedule.getCompanyId(),
            UserRole.INTERVIEWER,
            round.getRequiredSkills()
        );

        return candidates.stream()
            .filter(user -> !user.getId().equals(cancelledId))
            .filter(user -> availabilityService.isAvailable(user.getId(), schedule.getScheduledStartTime(), schedule.getDurationMinutes()))
            .filter(user -> !loadBalancingService.isAtCapacity(user.getId()))
            .min(Comparator.comparingLong(user -> loadBalancingService.getWeeklyInterviewCount(user.getId(), LocalDateTime.now().toLocalDate())));
    }

    private void logConflict(Long scheduleId, ConflictType type, String action) {
        ConflictLog log = ConflictLog.builder()
            .scheduleId(scheduleId)
            .conflictType(type)
            .resolvedBy("AUTO")
            .resolutionAction(action)
            .build();
        conflictLogRepository.save(log);
    }
}
