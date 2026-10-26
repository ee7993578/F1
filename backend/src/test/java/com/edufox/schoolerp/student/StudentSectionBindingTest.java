package com.edufox.schoolerp.student;

import com.edufox.schoolerp.student.shared.student_core.StudentRepository;
import com.edufox.schoolerp.student.shared.student_core.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class StudentSectionBindingTest {
    @Test void uploadSearchRequiresStaffAssignedSectionAndUsesRegisteredStatus() {
        StudentRepository repository = mock(StudentRepository.class);
        StudentService service = new StudentService(repository);
        when(repository.imageUploadSections("school", "staff", "2025 - 2026"))
            .thenReturn(List.of(Map.of("sectionId", 101)));
        service.imageUploadStudents("school", "staff", "2025 - 2026", 101);
        verify(repository).search("school", null, null, 101, "Registered", "2025 - 2026");
        assertThrows(ResponseStatusException.class,
            () -> service.imageUploadStudents("school", "staff", "2025 - 2026", 102));
        verify(repository, never()).search("school", null, null, 102, "Registered", "2025 - 2026");
    }

    @Test void exactLookupIsSessionAndStatusScoped() {
        StudentRepository repository = mock(StudentRepository.class);
        StudentService service = new StudentService(repository);
        service.infosheetLookup("school", "2025 - 2026", true, null, " BOOK1 ");
        verify(repository).infosheetLookup("school", "2025 - 2026", "Withdrawn", null, "BOOK1");
        service.infosheetLookup("school", "2025 - 2026", false, " ADM1 ", null);
        verify(repository).infosheetLookup("school", "2025 - 2026", "Registered", "ADM1", null);
        assertThrows(ResponseStatusException.class,
            () -> service.infosheetLookup("school", "", false, "ADM1", null));
        assertThrows(ResponseStatusException.class,
            () -> service.infosheetLookup("school", "2025 - 2026", false, "ADM1", "BOOK1"));
    }

    @Test void searchUsesSectionColumnNotClassColumn() {
        JdbcTemplate jdbc = mock(JdbcTemplate.class);
        new StudentRepository(jdbc).search("school", null, null, 101, "Registered", "2025 - 2026");
        verify(jdbc).queryForList(argThat((String sql) -> sql.contains("AND sm.sec_id=?")
            && !sql.contains("AND sm.class_id=?") && sql.contains("AND sm.session=?")),
            eq("school"), eq(101), eq("Registered"), eq("2025 - 2026"));
    }

    @Test void uploadOptionsCombinePermissionClassesAndTeacherSectionsWithinSessionMappings() {
        JdbcTemplate jdbc = mock(JdbcTemplate.class);
        when(jdbc.queryForList(contains("FROM TB_FacultyMaster"), eq(String.class), eq("school"), eq("staff")))
            .thenReturn(List.of("7"));
        when(jdbc.queryForList(contains("FROM roles_permission_efox"), eq(String.class), eq("school"), eq("school"), eq("7")))
            .thenReturn(List.of("10, 11", "10"));
        when(jdbc.queryForList(contains("FROM Tb_ClassTeacher"), eq(String.class), eq("school"), eq("2025 - 2026"), eq("7")))
            .thenReturn(List.of("201"));
        when(jdbc.queryForList(contains("FROM TB_Class c"), eq("school"), eq("2025"), eq("2026")))
            .thenReturn(List.of(Map.of("sectionId", 101, "classId", 10),
                Map.of("sectionId", 201, "classId", 20), Map.of("sectionId", 202, "classId", 20)));
        List<Map<String, Object>> rows = new StudentRepository(jdbc).imageUploadSections("school", "staff", "2025 - 2026");
        assertEquals(List.of(101, 201), rows.stream().map(row -> row.get("sectionId")).toList());
        verify(jdbc).queryForList(argThat((String sql) -> sql.contains("m.status=1")
            && sql.contains("m.from_year<=?") && sql.contains("m.to_year>=?")
            && sql.contains("m.school_id=c.school_id")), eq("school"), eq("2025"), eq("2026"));
    }
}
