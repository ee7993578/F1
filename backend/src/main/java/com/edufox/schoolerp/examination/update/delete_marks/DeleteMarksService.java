package com.edufox.schoolerp.examination.update.delete_marks;
import com.edufox.schoolerp.examination.report.examination_report.*;
import com.edufox.schoolerp.examination.setup.exam_setup.*;
import com.edufox.schoolerp.examination.work.examination_work.*;

import com.edufox.schoolerp.config.DatabaseWriteGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DeleteMarksService {
    private final DeleteMarksRepository repository;
    private final DatabaseWriteGuard writeGuard;

    public Map<String, Object> bootstrap(String school, String session) { return repository.bootstrap(school, session); }
    public List<Map<String, Object>> terms(String school, String session, int sectionId) { return repository.terms(school, session, sectionId); }
    public List<Map<String, Object>> subjects(String school, String session, int sectionId) { return repository.subjects(school, session, sectionId); }
    public List<Map<String, Object>> examTypes(String school, String session, int sectionId, int termId) { return repository.examTypes(school, session, sectionId, termId); }
    public Map<String, Object> classRows(String school, String session, int sectionId, String marksType, int examTypeId, int subjectId) { return repository.classRows(school, session, sectionId, marksType, examTypeId, subjectId); }
    public List<Map<String, Object>> studentSearch(String school, String session, String prefix) { return repository.studentSearch(school, session, prefix); }
    public List<Map<String, Object>> studentRows(String school, String session, String registrationNo) { return repository.studentRows(school, session, registrationNo); }
    @Transactional
    public void delete(String school, String session, String staff, List<Map<String, Object>> rows) { writeGuard.requireWritable(); repository.delete(school, session, staff, rows == null ? List.of() : rows); }
}
