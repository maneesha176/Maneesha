package com.interviewslot.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "interviewer_availability", indexes = {
    @Index(name = "idx_availability_user_date", columnList = "userId, date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewerAvailability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private LocalDate date;

    @ElementCollection
    @CollectionTable(name = "availability_slots", joinColumns = @JoinColumn(name = "availability_id"))
    @Column(name = "time_slot")
    private List<String> timeSlots; // Store as "HH:mm-HH:mm"

    private Boolean isRecurring;
    private LocalDateTime expiresAt;
}
