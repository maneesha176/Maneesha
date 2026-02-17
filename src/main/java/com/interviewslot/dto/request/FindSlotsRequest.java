package com.interviewslot.dto.request;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class FindSlotsRequest {
    private Long companyId;
    private Long candidateId;
    private Long pipelineId;
    private Integer roundNumber;
    private List<Long> interviewerIds;
    private Integer durationMinutes;
    private LocalDate startDate;
    private LocalDate endDate;
}
