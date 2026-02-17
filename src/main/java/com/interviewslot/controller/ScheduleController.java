package com.interviewslot.controller;

import com.interviewslot.dto.request.FindSlotsRequest;
import com.interviewslot.dto.request.ScheduleInterviewRequest;
import com.interviewslot.dto.response.AvailableSlotResponse;
import com.interviewslot.domain.model.InterviewSchedule;
import com.interviewslot.domain.enums.ScheduleStatus;
import com.interviewslot.repository.InterviewScheduleRepository;
import com.interviewslot.repository.CandidateRepository;
import com.interviewslot.service.SchedulingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final SchedulingService schedulingService;
    private final InterviewScheduleRepository scheduleRepository;
    private final CandidateRepository candidateRepository;

    @PostMapping("/find-slots")
    public ResponseEntity<List<AvailableSlotResponse>> findSlots(@RequestBody FindSlotsRequest request) {
        return ResponseEntity.ok(schedulingService.findAvailableSlots(request));
    }

    @PostMapping
    public ResponseEntity<InterviewSchedule> createSchedule(@RequestBody ScheduleInterviewRequest request) {
        InterviewSchedule schedule = InterviewSchedule.builder()
            .candidate(candidateRepository.findById(request.getCandidateId()).orElseThrow())
            .pipelineId(request.getPipelineId())
            .roundNumber(request.getRoundNumber())
            .scheduledStartTime(request.getScheduledTime())
            .scheduledEndTime(request.getScheduledTime().plusHours(1)) // Default 1 hour
            .status(ScheduleStatus.SCHEDULED)
            .interviewers(request.getInterviewerIds())
            .meetingLink("https://zoom.us/j/" + System.currentTimeMillis())
            .build();
        
        return ResponseEntity.ok(scheduleRepository.save(schedule));
    }

    @PutMapping("/{scheduleId}/cancel")
    public ResponseEntity<Void> cancelSchedule(@PathVariable Long scheduleId) {
        InterviewSchedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        schedule.setStatus(ScheduleStatus.CANCELLED);
        scheduleRepository.save(schedule);
        return ResponseEntity.ok().build();
    }
}
