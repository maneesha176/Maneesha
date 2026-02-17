package com.interviewslot.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String domain;
    private String size;
    private String industry;

    private LocalTime workingHoursStart;
    private LocalTime workingHoursEnd;
    private String timezone;
    private Integer maxInterviewsPerDay;

    @OneToMany(mappedBy = "company")
    private List<User> users;

    @OneToMany(mappedBy = "company")
    private List<InterviewPipeline> pipelines;
}
