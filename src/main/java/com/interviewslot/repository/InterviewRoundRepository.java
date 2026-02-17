package com.interviewslot.repository;

import com.interviewslot.domain.model.InterviewRound;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InterviewRoundRepository extends JpaRepository<InterviewRound, Long> {
    Optional<InterviewRound> findByPipelineIdAndRoundNumber(Long pipelineId, Integer roundNumber);
}
