package com.edufox.schoolerp.timetable;

import com.edufox.schoolerp.config.DatabaseWriteGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TimetableService {
    private final TimetableRepository repository;
    private final DatabaseWriteGuard writeGuard;
    private final VirtualClassRepository virtualClasses;

    public Map<String, Object> bootstrap(String school, String session) { return repository.bootstrap(school, session); }
    public List<Map<String, Object>> assignment(String school, String session, int section, int faculty, String day, String ttUid) { return repository.assignment(school, session, section, faculty, day, ttUid); }
    public List<Map<String, Object>> subjects(String school, String session, int section) { return repository.subjects(school, session, section); }
    @Transactional public int saveAssignment(String school, String staff, Map<String, Object> body) { writeGuard.requireWritable(); return repository.saveAssignment(school, staff, body); }
    public Map<String, Object> alternate(String school, String session, int faculty, String ttUid) { return repository.alternate(school, session, faculty, ttUid); }
    @Transactional public int saveAlternate(String school, String staff, Map<String, Object> body) { writeGuard.requireWritable(); return repository.saveAlternate(school, staff, body); }
    public Map<String, Object> activity(String school, String session, int classId) { return repository.activity(school, session, classId); }
    @Transactional public int saveActivity(String school, String staff, Map<String, Object> body) { writeGuard.requireWritable(); return repository.saveActivity(school, staff, body); }
    public Map<String, Object> holidays(String school, String session) { return repository.holidays(school, session); }
    @Transactional public int saveHoliday(String school, String staff, Map<String, Object> body) { writeGuard.requireWritable(); return repository.saveHoliday(school, staff, body); }
    @Transactional public int deleteHoliday(String school, String staff, Map<String, Object> body) { writeGuard.requireWritable(); return repository.deleteHoliday(school, staff, body); }
    public Map<String, Object> mapping(String school, String session, int faculty, int section) { return repository.mapping(school, session, faculty, section); }
    @Transactional public int saveMapping(String school, String staff, Map<String, Object> body) { writeGuard.requireWritable(); return repository.saveMapping(school, staff, body); }
    public Map<String,Object> virtualClassOptions(String school,String staff) { return virtualClasses.options(school,staff); }
    public List<Map<String,Object>> virtualClassStudents(String school,int section,int subject) { return virtualClasses.candidates(school,virtualClasses.currentSession(school),section,subject); }
    public Map<String, Object> virtualClass(String school, String staff, String session, Map<String, Object> filter) { return virtualClasses.read(school,staff,session,filter); }
    @Transactional public int saveVirtualClass(String school, String staff, Map<String, Object> body) { writeGuard.requireWritable(); return virtualClasses.save(school, staff, body); }
    @Transactional public int deleteVirtualClass(String school, String staff, Map<String, Object> body) { writeGuard.requireWritable(); return virtualClasses.delete(school, staff, body); }
    @Transactional public int saveConfig(String school, String staff, Map<String, Object> body) { writeGuard.requireWritable(); return repository.saveConfig(school, staff, body); }
    @Transactional public int deleteConfig(String school, String staff, Map<String, Object> body) { writeGuard.requireWritable(); return repository.deleteConfig(school, staff, body); }
    public Map<String, Object> report(String school, String session, String type, Map<String, String> params) { return repository.report(school, session, type, params); }
}
