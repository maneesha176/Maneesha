package com.interviewslot.service;

import com.interviewslot.domain.exception.ResourceNotFoundException;
import com.interviewslot.domain.model.Company;
import com.interviewslot.dto.TimeSlot;
import com.interviewslot.dto.request.FindSlotsRequest;
import com.interviewslot.dto.response.AvailableSlotResponse;
import com.interviewslot.repository.CompanyRepository;
import com.interviewslot.repository.InterviewScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SchedulingService {

    private final AvailabilityService availabilityService;
    private final LoadBalancingService loadBalancingService;
    private final CompanyRepository companyRepository;
    private final InterviewScheduleRepository scheduleRepository;

    public List<AvailableSlotResponse> findAvailableSlots(FindSlotsRequest request) {
        log.info("Finding slots for candidate {} with {} interviewers", 
            request.getCandidateId(), request.getInterviewerIds().size());
        
        long startTime = System.currentTimeMillis();

        // 1. Fetch availability
        Map<Long, Set<TimeSlot>> interviewerSlots = availabilityService.getInterviewerAvailability(
            request.getInterviewerIds(), 
            request.getStartDate(), 
            request.getEndDate()
        );

        Set<TimeSlot> candidateSlots = availabilityService.getCandidateAvailability(
            request.getCandidateId(), 
            request.getStartDate(), 
            request.getEndDate()
        );

        // 2. Find common slots
        List<TimeSlot> commonSlots = findCommonSlots(interviewerSlots, candidateSlots);

        // 3. Filter by business rules
        List<TimeSlot> validSlots = applyBusinessRules(commonSlots, request.getInterviewerIds(), request.getCompanyId());

        // 4. Rank slots
        List<AvailableSlotResponse> rankedSlots = rankSlots(validSlots, request.getInterviewerIds());

        log.info("Slot finding took {}ms. Found {} valid slots.", 
            (System.currentTimeMillis() - startTime), rankedSlots.size());

        return rankedSlots.stream().limit(10).collect(Collectors.toList());
    }

    private List<TimeSlot> findCommonSlots(Map<Long, Set<TimeSlot>> interviewerSlots, Set<TimeSlot> candidateSlots) {
        if (interviewerSlots.isEmpty()) return Collections.emptyList();

        Iterator<Set<TimeSlot>> it = interviewerSlots.values().iterator();
        Set<TimeSlot> common = new HashSet<>(it.next());

        while (it.hasNext()) {
            common.retainAll(it.next());
        }

        common.retainAll(candidateSlots);
        return new ArrayList<>(common);
    }

    private List<TimeSlot> applyBusinessRules(List<TimeSlot> slots, List<Long> interviewerIds, Long companyId) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        return slots.stream()
            .filter(slot -> isWithinWorkingHours(slot, company))
            .filter(slot -> hasBufferTime(slot, interviewerIds))
            .filter(slot -> !exceedsInterviewerDailyLimit(slot, interviewerIds, company.getMaxInterviewsPerDay()))
            .collect(Collectors.toList());
    }

    private boolean isWithinWorkingHours(TimeSlot slot, Company company) {
        LocalTime start = slot.getStartTime().toLocalTime();
        LocalTime end = slot.getEndTime().toLocalTime();
        return !start.isBefore(company.getWorkingHoursStart()) && !end.isAfter(company.getWorkingHoursEnd());
    }

    private boolean hasBufferTime(TimeSlot slot, List<Long> interviewerIds) {
        // Implement 15 min buffer logic
        // For simplicity, we assume if they are marked available in discrete slots, they have buffer
        // In a real system, we'd check adjacent schedules
        return true; 
    }

    private boolean exceedsInterviewerDailyLimit(TimeSlot slot, List<Long> interviewerIds, Integer limit) {
        if (limit == null) limit = 3;
        for (Long id : interviewerIds) {
            // Check daily count for each interviewer
            long count = scheduleRepository.countInterviewsForUserInRange(
                id, 
                slot.getStartTime().toLocalDate().atStartOfDay(), 
                slot.getStartTime().toLocalDate().plusDays(1).atStartOfDay()
            );
            if (count >= limit) return true;
        }
        return false;
    }

    private List<AvailableSlotResponse> rankSlots(List<TimeSlot> slots, List<Long> interviewerIds) {
        Map<Long, Long> interviewerLoadMap = loadBalancingService.getInterviewerWeeklyLoad(interviewerIds);
        
        return slots.stream()
            .sorted(Comparator
                .comparing(TimeSlot::getStartTime)
                .thenComparing(slot -> calculateLoadScore(slot, interviewerLoadMap))
            )
            .map(slot -> AvailableSlotResponse.builder()
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .interviewerIds(interviewerIds)
                .loadScore(calculateLoadScore(slot, interviewerLoadMap))
                .build()
            )
            .collect(Collectors.toList());
    }

    private double calculateLoadScore(TimeSlot slot, Map<Long, Long> loadMap) {
        return loadMap.values().stream().mapToLong(Long::longValue).average().orElse(0.0);
    }
}
