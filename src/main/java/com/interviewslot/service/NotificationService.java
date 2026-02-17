package com.interviewslot.service;

import com.interviewslot.domain.model.InterviewRound;
import com.interviewslot.domain.model.InterviewSchedule;
import com.interviewslot.domain.model.User;
import com.interviewslot.dto.response.AvailableSlotResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
public class NotificationService {

    public void alertHRNoAvailability(InterviewSchedule schedule, InterviewRound round) {
        log.warn("No availability found for round {} of candidate {}", round.getRoundName(), schedule.getCandidate().getName());
    }

    public void sendSlotOptionsToCandidate(Long candidateId, List<AvailableSlotResponse> slots) {
        log.info("Sending {} slot options to candidate {}", slots.size(), candidateId);
    }

    public void notifyInterviewerReplacement(InterviewSchedule schedule, User replacement) {
        log.info("Interviewer replaced in schedule {}. New interviewer: {}", schedule.getId(), replacement.getName());
    }

    public void alertHRForManualReschedule(InterviewSchedule schedule) {
        log.error("Manual rescheduling required for schedule {}", schedule.getId());
    }
}
