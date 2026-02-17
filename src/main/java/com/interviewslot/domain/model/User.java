package com.interviewslot.domain.model;

import com.interviewslot.domain.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;
    private String name;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @ElementCollection
    @CollectionTable(name = "user_skills", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "skill")
    private Set<String> skills;

    private Integer maxInterviewsPerWeek;

    @ElementCollection
    @CollectionTable(name = "user_preferences", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "preferred_time_slot")
    private Set<String> preferredTimeSlots;
}
