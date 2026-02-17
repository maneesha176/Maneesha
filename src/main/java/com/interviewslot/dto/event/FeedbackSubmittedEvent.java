package com.interviewslot.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FeedbackSubmittedEvent {
    private final Long scheduleId;
}
