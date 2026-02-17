package com.interviewslot.service;

import com.interviewslot.domain.enums.CandidateStage;
import com.interviewslot.domain.enums.CandidateStatus;
import com.interviewslot.domain.enums.FeedbackDecision;
import com.interviewslot.domain.enums.UserRole;
import com.interviewslot.domain.model.*;
import com.interviewslot.dto.event.FeedbackSubmittedEvent;
import com.interviewslot.dto.request.FindSlotsRequest;
import com.interviewslot.dto.response.AvailableSlotResponse;
import com.interviewslot.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutoSchedulingService {

    private final InterviewScheduleRepository scheduleRepository;
    private final FeedbackRepository feedbackRepository;
    private final CandidateRepository candidateRepository;
    private final InterviewRoundRepository roundRepository;
    private final UserRepository userRepository;
    private final LoadBalancingService loadBalancingService;
    private final SchedulingService schedulingService;
    private final NotificationService notificationService;

    @EventListener
    @Async
    public void onFeedbackSubmitted(FeedbackSubmittedEvent event) {
        Long scheduleId = event.getScheduleId();
        InterviewSchedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();

        long feedbackCount = feedbackRepository.countByScheduleId(scheduleId);
        if (feedbackCount < schedule.getInterviewers().size()) {
            return; 
        }

        boolean candidatePassed = evaluateCandidatePerformance(scheduleId);

        if (candidatePassed) {
            scheduleNextRound(schedule);
        } else {
            rejectCandidate(schedule.getCandidate().getId());
        }
    }

    private boolean evaluateCandidatePerformance(Long scheduleId) {
        List<Feedback> feedbacks = feedbackRepository.findByScheduleId(scheduleId);
        double avgRating = feedbacks.stream().mapToInt(Feedback::getRating).average().orElse(0.0);
        long passVotes = feedbacks.stream()
            .filter(f -> f.getDecision() == FeedbackDecision.YES || f.getDecision() == FeedbackDecision.STRONG_YES)
            .count();

        return avgRating >= 3.5 && passVotes >= (feedbacks.size() / 2.0);
    }

    private void scheduleNextRound(InterviewSchedule currentSchedule) {
        Optional<InterviewRound> nextRoundOpt = roundRepository.findByPipelineIdAndRoundNumber(
            currentSchedule.getPipelineId(), 
            currentSchedule.getRoundNumber() + 1
        );

        if (nextRoundOpt.isEmpty()) {
            Candidate candidate = currentSchedule.getCandidate();
            candidate.setCurrentStage(CandidateStage.FINAL);
            candidate.setStatus(CandidateStatus.OFFER_ACCEPTED); // Simplification
            candidateRepository.save(candidate);
            return;
        }

        InterviewRound nextRound = nextRoundOpt.get();
        List<User> eligibleInterviewers = userRepository.findByCompanyIdAndRoleAndSkillsIn(
            currentSchedule.getCompanyId(),
            UserRole.INTERVIEWER,
            nextRound.getRequiredSkills()
        );

        List<Long> selectedInterviewerIds = loadBalancingService.selectInterviewers(eligibleInterviewers, nextRound.getRequiredInterviewerCount());

        FindSlotsRequest request = new FindSlotsRequest();
        request.setCandidateId(currentSchedule.getCandidate().getId());
        request.setInterviewerIds(selectedInterviewerIds);
        request.setDurationMinutes(nextRound.getDurationMinutes());
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(8));
        request.setCompanyId(currentSchedule.getCompanyId());

        List<AvailableSlotResponse> slots = schedulingService.findAvailableSlots(request);

        if (slots.isEmpty()) {
            notificationService.alertHRNoAvailability(currentSchedule, nextRound);
        } else {
            notificationService.sendSlotOptionsToCandidate(currentSchedule.getCandidate().getId(), slots);
        }
    }

    private void rejectCandidate(Long candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId).orElseThrow();
        candidate.setCurrentStage(CandidateStage.REJECTED);
        candidate.setStatus(CandidateStatus.DROPPED);
        candidateRepository.save(candidate);
    }
}
