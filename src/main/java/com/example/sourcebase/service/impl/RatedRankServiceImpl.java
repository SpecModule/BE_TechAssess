package com.example.sourcebase.service.impl;

import com.example.sourcebase.domain.Assess;
import com.example.sourcebase.domain.dto.resdto.AssessDetailResDto;
import com.example.sourcebase.domain.dto.resdto.AssessResDTO;
import com.example.sourcebase.mapper.AssessMapper;
import com.example.sourcebase.repository.IAssessRepository;
import com.example.sourcebase.service.IRatedRankService;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class RatedRankServiceImpl implements IRatedRankService {
    IAssessRepository assessRepository;
    AssessMapper assessMapper;
    public Map<Long, Object> getAverageValueBySelf(Long userId) {
        // 1. Lấy danh sách đánh giá SELF của userId
        Assess assess = assessRepository.getAssessBySelf(userId);
        AssessResDTO assessResDTO = assessMapper.toAssessResDto(assess);
        assessResDTO.setAssessDetails(assessResDTO.getAssessDetails().stream()
                .peek(assessDetail -> assessDetail.setAssessId(assessResDTO.getId()))
                .collect(Collectors.toList()));
        // 2. Tính giá trị trung bình của các tiêu chí (criteriaID giống nhau)
        Map<Long, Object> result = new HashMap<>();
        Map<Long, List<AssessDetailResDto>> groupedByCriteria = assessResDTO.getAssessDetails().stream()
                .collect(Collectors.groupingBy(detail -> detail.getCriteria().getId()));

        groupedByCriteria.forEach((criteriaId, assessDetails) -> {
            double averageValue = assessDetails.stream()
                    .mapToDouble(AssessDetailResDto::getValue)
                    .average()
                    .orElse(0.0);

            result.put(criteriaId, Math.round(averageValue));
        });
        // 3. Trả về kết quả
        return result;
    }
}

