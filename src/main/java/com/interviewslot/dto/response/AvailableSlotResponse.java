package com.interviewslot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableSlotResponse {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<Long> interviewerIds;
    private Double loadScore;
}
