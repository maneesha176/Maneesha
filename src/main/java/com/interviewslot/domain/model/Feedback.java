package com.interviewslot.domain.model;

import com.interviewslot.domain.enums.FeedbackDecision;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long scheduleId;
    private Long interviewerId;
    private Long candidateId;

    private Integer rating; // 1-5

    @Enumerated(EnumType.STRING)
    private FeedbackDecision decision;

    @Column(length = 2000)
    private String comments;
    
    private Integer technicalSkills;
    private Integer communicationSkills;

    private LocalDateTime submittedAt;

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
    }
}
