package com.interviewslot.dto.request;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ScheduleInterviewRequest {
    private Long candidateId;
    private Long pipelineId;
    private Integer roundNumber;
    private LocalDateTime scheduledTime;
    private List<Long> interviewerIds;
}
