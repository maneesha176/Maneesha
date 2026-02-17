package com.interviewslot.service;

import com.interviewslot.domain.model.Feedback;
import com.interviewslot.domain.model.InterviewSchedule;
import com.interviewslot.dto.event.FeedbackSubmittedEvent;
import com.interviewslot.dto.request.SubmitFeedbackRequest;
import com.interviewslot.repository.FeedbackRepository;
import com.interviewslot.repository.InterviewScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final InterviewScheduleRepository scheduleRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Feedback submitFeedback(SubmitFeedbackRequest request) {
        InterviewSchedule schedule = scheduleRepository.findById(request.getScheduleId()).orElseThrow();
        
        Feedback feedback = Feedback.builder()
            .scheduleId(request.getScheduleId())
            .interviewerId(request.getInterviewerId())
            .candidateId(schedule.getCandidate().getId())
            .decision(request.getDecision())
            .rating(request.getRating())
            .comments(request.getComments())
            .technicalSkills(request.getTechnicalSkills())
            .communicationSkills(request.getCommunicationSkills())
            .build();

        Feedback savedFeedback = feedbackRepository.save(feedback);
        
        // Publish event for auto-scheduling
        eventPublisher.publishEvent(new FeedbackSubmittedEvent(request.getScheduleId()));
        
        return savedFeedback;
    }
}
