package com.edufox.schoolerp.transport;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transport")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class TransportStudentController {
    private final JdbcTemplate jdbc;

    private static String school(Authentication a) { return String.valueOf(((Claims) a.getDetails()).get("schoolId")); }

    @GetMapping("/students-list")
    public List<Map<String, Object>> list(@RequestParam String session,
                                          @RequestParam(defaultValue = "0") String classId,
                                          @RequestParam(defaultValue = "Registered") String status,
                                          Authentication a) {
        String sql = "SELECT st.stu_id AS id, st.stu_id AS studentId, st.registration_no AS registrationNo, " +
                "'('+st.registration_no+')'+UPPER(COALESCE(so.first_name,''))+' '+UPPER(COALESCE(so.mid_name,''))+' '+UPPER(COALESCE(so.last_name,''))+UPPER(COALESCE('--'+so.father_name,''))+UPPER(COALESCE(so.mother_name,'')) AS name " +
                "FROM TB_StudentMaster st JOIN student_other_info so ON so.school_id=st.school_id " +
                "AND so.registration_no=st.registration_no AND so.uid=st.uid " +
                "WHERE st.school_id=? AND st.session=? AND st.status=?" +
                (classId.isBlank() || "0".equals(classId) ? "" : " AND st.sec_id=?") +
                " ORDER BY so.first_name,so.mid_name,so.last_name,st.registration_no";
        if (classId.isBlank() || "0".equals(classId)) return jdbc.queryForList(sql, school(a), session, status);
        return jdbc.queryForList(sql, school(a), session, status, classId);
    }
}
