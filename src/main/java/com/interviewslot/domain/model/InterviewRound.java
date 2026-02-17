package com.interviewslot.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "interview_rounds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewRound {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pipeline_id")
    private InterviewPipeline pipeline;

    private Integer roundNumber;
    private String roundName;
    private Integer durationMinutes;
    private Integer requiredInterviewerCount;

    @ElementCollection
    @CollectionTable(name = "round_required_skills", joinColumns = @JoinColumn(name = "round_id"))
    @Column(name = "skill")
    private Set<String> requiredSkills;

    private Boolean isKnockout;
}
