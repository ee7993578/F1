package com.edufox.schoolerp.student;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/internal/student-schema")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class StudentSchemaProbeController {
    private final JdbcTemplate jdbc;
    @GetMapping
    public Map<String, Object> inspect() {
        List<Map<String, Object>> columns = jdbc.queryForList("SELECT TABLE_NAME AS tableName, COLUMN_NAME AS columnName, DATA_TYPE AS dataType, CHARACTER_MAXIMUM_LENGTH AS maxLength, IS_NULLABLE AS nullable, COLUMN_DEFAULT AS defaultValue FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME IN ('TB_StudentMaster','student_other_info','student_reg','TB_Class','class_name','onlineStudentRegistration') ORDER BY TABLE_NAME, ORDINAL_POSITION");
        List<Map<String, Object>> procedures = jdbc.queryForList("SELECT OBJECT_NAME(p.object_id) AS procedureName, p.name AS parameterName, TYPE_NAME(p.user_type_id) AS dataType, p.max_length AS maxLength, p.is_output AS isOutput, p.parameter_id AS parameterId FROM sys.parameters p WHERE OBJECT_NAME(p.object_id) IN ('insert_stu_reg_detail','insert_stu_reg_detail_new','update_stu_info_new') ORDER BY procedureName, p.parameter_id");
        return Map.of("columns", columns, "procedures", procedures);
    }
}