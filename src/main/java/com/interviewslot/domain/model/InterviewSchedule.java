package com.interviewslot.domain.model;

import com.interviewslot.domain.enums.ScheduleStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "interview_schedules", indexes = {
    @Index(name = "idx_schedules_start_time", columnList = "scheduledStartTime"),
    @Index(name = "idx_schedules_candidate", columnList = "candidate_id"),
    @Index(name = "idx_schedules_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    private Long pipelineId;
    private Integer roundNumber;

    private LocalDateTime scheduledStartTime;
    private LocalDateTime scheduledEndTime;

    @Enumerated(EnumType.STRING)
    private ScheduleStatus status;

    @ElementCollection
    @CollectionTable(name = "schedule_interviewers", joinColumns = @JoinColumn(name = "schedule_id"))
    @Column(name = "user_id")
    private List<Long> interviewers;

    private String meetingLink;
    private Boolean feedbackSubmitted;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (feedbackSubmitted == null) feedbackSubmitted = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void replaceInterviewer(Long oldId, Long newId) {
        if (interviewers != null) {
            int index = interviewers.indexOf(oldId);
            if (index != -1) {
                interviewers.set(index, newId);
            }
        }
    }

    public int getDurationMinutes() {
        if (scheduledStartTime != null && scheduledEndTime != null) {
            return (int) java.time.Duration.between(scheduledStartTime, scheduledEndTime).toMinutes();
        }
        return 0;
    }

    public Long getCompanyId() {
        // This is a bit of a shortcut, ideally we'd have companyId directly or via candidate/pipeline
        // For simplicity in the demo algorithm:
        return 1L; // Placeholder or add companyId field
    }
}
