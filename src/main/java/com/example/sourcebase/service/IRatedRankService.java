package com.example.sourcebase.service;

import java.util.List;
import java.util.Map;

public interface IRatedRankService {
    Map<Long, Object> getAverageValueByTeam(Long userId);
}
