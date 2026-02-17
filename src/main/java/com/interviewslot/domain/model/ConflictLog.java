package com.interviewslot.domain.model;

import com.interviewslot.domain.enums.ConflictType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "conflict_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConflictLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long scheduleId;

    @Enumerated(EnumType.STRING)
    private ConflictType conflictType;

    private String resolvedBy;
    private String resolutionAction;
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}
