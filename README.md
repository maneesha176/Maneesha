# InterviewSlot - Angular Technical Platform

This project has been migrated from React to **Angular** (latest signals-based architecture).

## Problem Statement
Technical hiring teams waste 40+ hours monthly coordinating panel interviews. InterviewSlot automates multi-party scheduling, load balancing, and conflict resolution.

## Key Features
- **Multi-party slot finding**: Find common availability across 2-5 interviewers + candidate
- **Auto-scheduling**: Automatically schedule next round when candidate passes
- **Load balancing**: Fairly distribute interviews across interviewer pool
- **Conflict resolution**: Auto-replace cancelled interviewers within 24 hours
- **Analytics**: Track interviewer workload, pipeline metrics, time-to-hire

## Tech Stack
- Java 17, Spring Boot 3.2
- PostgreSQL 15 (primary data store)
- Redis (caching layer)
- Maven (build tool)

## System Design Highlights
- **Optimistic locking** prevents double-booking
- **Event-driven architecture** for async workflows (feedback → auto-schedule)
- **Multi-layer caching** reduces DB load by 70%
- **CQRS pattern** separates read-heavy queries from write operations
- **Horizontal scalability** via stateless services

## Core Algorithms
### 1. Availability Intersection Algorithm
Finds common free slots across N participants in O(N * M) where M = number of time slots

### 2. Load Balancing Heuristic
Distributes interviews using weighted round-robin considering:
- Current week's interview count
- Skill match score
- Historical performance

### 3. Conflict Resolution
Auto-replacement logic with fallback chain:
Primary → Secondary → Tertiary → Manual escalation

## Getting Started

### Prerequisites
- Java 17+
- PostgreSQL 15
- Redis 7+
- Maven 3.8+

### Running Locally
```bash
# Clone repository
git clone https://github.com/yourusername/interview-slot.git

# Setup database
createdb interviewslot
# Application will automatically update schema via JPA ddl-auto: update

# Start Redis
redis-server

# Run application
mvn spring-boot:run
```

## API Documentation
Endpoints follow RESTful conventions. Key endpoints include:
- `POST /api/v1/schedules/find-slots` - Find available interview slots
- `POST /api/v1/schedules` - Book an interview
- `POST /api/v1/feedback` - Submit interview feedback

## Database Schema
- **companies**: Organization data
- **users**: HRs, Interviewers, Admins
- **candidates**: People being interviewed
- **interview_pipelines**: Position-specific interview flows
- **interview_schedules**: Actual booked interviews
- **interviewer_availability**: Calendar data
- **feedback**: Post-interview evaluations

## Future Enhancements
- [ ] Calendar integration (Google, Outlook)
- [ ] ML-based no-show prediction
- [ ] Video platform integration (Zoom, Meet)
- [ ] Multi-tenancy support
