package com.interviewslot.dto.request;

import com.interviewslot.domain.enums.FeedbackDecision;
import lombok.Data;

@Data
public class SubmitFeedbackRequest {
    private Long scheduleId;
    private Long interviewerId;
    private FeedbackDecision decision;
    private Integer rating;
    private String comments;
    private Integer technicalSkills;
    private Integer communicationSkills;
}
