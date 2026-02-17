package com.interviewslot.service;

import com.interviewslot.domain.model.User;
import com.interviewslot.repository.InterviewScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoadBalancingService {

    private final InterviewScheduleRepository scheduleRepository;

    @Cacheable(value = "interviewer-load", key = "#userId + '-' + #weekStart")
    public long getWeeklyInterviewCount(Long userId, LocalDate weekStart) {
        LocalDateTime start = weekStart.atStartOfDay();
        LocalDateTime end = weekStart.plusDays(7).atStartOfDay();
        return scheduleRepository.countInterviewsForUserInRange(userId, start, end);
    }

    public Map<Long, Long> getInterviewerWeeklyLoad(List<Long> interviewerIds) {
        LocalDate weekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        Map<Long, Long> loadMap = new HashMap<>();
        for (Long id : interviewerIds) {
            loadMap.put(id, getWeeklyInterviewCount(id, weekStart));
        }
        return loadMap;
    }

    public List<Long> selectInterviewers(List<User> eligibleInterviewers, int count) {
        LocalDate weekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        
        return eligibleInterviewers.stream()
            .sorted(Comparator.comparingLong(u -> getWeeklyInterviewCount(u.getId(), weekStart)))
            .limit(count)
            .map(User::getId)
            .collect(Collectors.toList());
    }

    public boolean isAtCapacity(Long userId) {
        LocalDate weekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        // Assume default max 15 per week if not specified
        return getWeeklyInterviewCount(userId, weekStart) >= 15;
    }
}
