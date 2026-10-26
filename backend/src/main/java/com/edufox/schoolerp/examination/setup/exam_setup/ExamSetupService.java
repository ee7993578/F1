package com.edufox.schoolerp.examination.setup.exam_setup;
import com.edufox.schoolerp.examination.report.examination_report.*;
import com.edufox.schoolerp.examination.update.delete_marks.*;
import com.edufox.schoolerp.examination.work.examination_work.*;

import com.edufox.schoolerp.config.DatabaseWriteGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExamSetupService {
    private final ExamSetupRepository repository;
    private final DatabaseWriteGuard writeGuard;

    public Map<String, Object> bootstrap(String school, String staff) { return repository.bootstrap(school, staff); }
    public List<Map<String, Object>> options(String school, String type, Map<String, String> params) { return repository.options(school, type, params); }
    public Map<String, Object> data(String school, String page, Map<String, String> params) { return repository.data(school, page, params); }
    @Transactional public void save(String school, String staff, String page, Map<String, Object> body) { writeGuard.requireWritable(); repository.save(school, staff, page, body); }
    @Transactional public void delete(String school, String page, Map<String, Object> body) { writeGuard.requireWritable(); repository.delete(school, page, body); }
}
