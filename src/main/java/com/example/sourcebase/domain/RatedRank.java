package com.example.sourcebase.domain;

import com.example.sourcebase.domain.enumeration.ERank;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "rated_rank")
public class RatedRank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "total_point", precision = 15, scale = 2)
    private BigDecimal totalPoint;

    @Enumerated(EnumType.STRING)
    @Column(name = "rank", length = 45)
    private ERank rank;

    @Column(name = "level_up_recommend")
    private Integer levelUpRecommend;

}