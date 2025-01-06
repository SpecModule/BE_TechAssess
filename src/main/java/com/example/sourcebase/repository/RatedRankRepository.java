package com.example.sourcebase.repository;

import com.example.sourcebase.domain.RatedRank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatedRankRepository extends JpaRepository<RatedRank, Long> {
}