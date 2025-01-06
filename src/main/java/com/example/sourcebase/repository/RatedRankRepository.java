package com.example.sourcebase.repository;

import com.example.sourcebase.domain.Assess;
import com.example.sourcebase.domain.RatedRank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RatedRankRepository extends JpaRepository<RatedRank, Long> {
    @Query("SELECT a FROM Assess a WHERE a.toUser.id = :userId AND a.assessmentType = 'SELF'")
    List<Assess> getAssessBySelf(Long userId);
}