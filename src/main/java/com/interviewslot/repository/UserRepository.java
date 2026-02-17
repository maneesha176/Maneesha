package com.interviewslot.repository;

import com.interviewslot.domain.model.User;
import com.interviewslot.domain.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.skills s WHERE u.company.id = :companyId AND u.role = :role AND s IN :skills")
    List<User> findByCompanyIdAndRoleAndSkillsIn(
        @Param("companyId") Long companyId, 
        @Param("role") UserRole role, 
        @Param("skills") Set<String> skills
    );

    List<User> findByCompanyIdAndRole(Long companyId, UserRole role);
}
