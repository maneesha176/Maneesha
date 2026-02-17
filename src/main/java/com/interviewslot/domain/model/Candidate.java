package com.interviewslot.domain.model;

import com.interviewslot.domain.enums.CandidateStage;
import com.interviewslot.domain.enums.CandidateStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "candidates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;
    private String appliedFor;

    @Enumerated(EnumType.STRING)
    private CandidateStage currentStage;

    @Enumerated(EnumType.STRING)
    private CandidateStatus status;
}
