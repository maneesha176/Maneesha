package com.interviewslot.service;

import com.interviewslot.domain.model.InterviewerAvailability;
import com.interviewslot.dto.TimeSlot;
import com.interviewslot.repository.InterviewerAvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final InterviewerAvailabilityRepository availabilityRepository;

    @Cacheable(value = "interviewer-availability", key = "#userId + '-' + #startDate + '-' + #endDate")
    public Map<Long, Set<TimeSlot>> getInterviewerAvailability(List<Long> userIds, LocalDate startDate, LocalDate endDate) {
        Map<Long, Set<TimeSlot>> availabilityMap = new HashMap<>();

        for (Long userId : userIds) {
            List<InterviewerAvailability> availabilities = availabilityRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
            Set<TimeSlot> slots = new HashSet<>();

            for (InterviewerAvailability availability : availabilities) {
                for (String slotStr : availability.getTimeSlots()) {
                    slots.add(parseTimeSlot(availability.getDate(), slotStr));
                }
            }
            availabilityMap.put(userId, slots);
        }

        return availabilityMap;
    }

    public Set<TimeSlot> getCandidateAvailability(Long candidateId, LocalDate startDate, LocalDate endDate) {
        // In a real system, this might fetch from a candidate portal or external calendar
        // For this demo, we'll assume candidates are available during standard working hours
        Set<TimeSlot> slots = new HashSet<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            // Default 9 AM to 6 PM availability for candidates
            for (int hour = 9; hour < 18; hour++) {
                slots.add(new TimeSlot(
                    current.atTime(hour, 0),
                    current.atTime(hour + 1, 0)
                ));
            }
            current = current.plusDays(1);
        }
        return slots;
    }

    public boolean isAvailable(Long userId, LocalDateTime startTime, int durationMinutes) {
        Optional<InterviewerAvailability> availabilityOpt = availabilityRepository.findByUserIdAndDate(userId, startTime.toLocalDate());
        if (availabilityOpt.isEmpty()) return false;

        String targetSlot = formatAsSlot(startTime, startTime.plusMinutes(durationMinutes));
        return availabilityOpt.get().getTimeSlots().contains(targetSlot);
    }

    private TimeSlot parseTimeSlot(LocalDate date, String slotStr) {
        String[] parts = slotStr.split("-");
        LocalTime start = LocalTime.parse(parts[0]);
        LocalTime end = LocalTime.parse(parts[1]);
        return new TimeSlot(date.atTime(start), date.atTime(end));
    }

    private String formatAsSlot(LocalDateTime start, LocalDateTime end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return start.format(formatter) + "-" + end.format(formatter);
    }
}
