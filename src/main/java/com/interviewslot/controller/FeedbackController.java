package com.interviewslot.controller;

import com.interviewslot.dto.request.SubmitFeedbackRequest;
import com.interviewslot.domain.model.Feedback;
import com.interviewslot.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<Feedback> submitFeedback(@RequestBody SubmitFeedbackRequest request) {
        return ResponseEntity.ok(feedbackService.submitFeedback(request));
    }
}
