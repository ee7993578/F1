package com.edufox.schoolerp.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final DashboardRepository repository;

    public Map<String, Object> dashboard(String schoolId, String type, String session, int days) {
        return repository.dashboard(schoolId, type, session, days);
    }
}
