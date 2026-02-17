package com.interviewslot.repository;

import com.interviewslot.domain.model.InterviewPipeline;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewPipelineRepository extends JpaRepository<InterviewPipeline, Long> {
}
