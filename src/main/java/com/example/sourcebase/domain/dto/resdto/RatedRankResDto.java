package com.example.sourcebase.domain.dto.resdto;

import com.example.sourcebase.domain.dto.resdto.user.UserResDTO;
import com.example.sourcebase.domain.enumeration.ERank;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO for {@link com.example.sourcebase.domain.RatedRank}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RatedRankResDto implements Serializable {
    private Long id;
    private UserResDTO user;
    private Instant createdAt;
    private BigDecimal totalPoint;
    private ERank rank;
    private Integer levelUpRecommend;
}