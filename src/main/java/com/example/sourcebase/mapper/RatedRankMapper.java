package com.example.sourcebase.mapper;

import com.example.sourcebase.domain.RatedRank;
import com.example.sourcebase.domain.dto.reqdto.RatedRankReqDto;
import com.example.sourcebase.domain.dto.resdto.RatedRankResDto;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface RatedRankMapper {
    RatedRank toEntity(RatedRankReqDto ratedRankReqDto);

    RatedRankResDto toResDto(RatedRank ratedRank);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    RatedRank partialUpdate(RatedRankReqDto ratedRankReqDto, @MappingTarget RatedRank ratedRank);
}