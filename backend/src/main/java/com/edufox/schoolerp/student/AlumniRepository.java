package com.edufox.schoolerp.student;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class AlumniRepository {
    private final JdbcTemplate jdbc;
    public List<Map<String,Object>> list(String schoolId) {
        return jdbc.queryForList("""
            SELECT ID AS id, FullName, StudentId, Batch, CONVERT(varchar(10),DOB,23) AS DOB,
                   Qualification, CurrentStatus, EmailAddress, PhoneNumber, Gender,
                   Address AS PermanentAddress, AlumniPhoto, status
            FROM Alumni WHERE school_id=? AND ISNULL(status,'Active')<>'Deleted'
            ORDER BY ID DESC
            """, schoolId);
    }
    @Transactional
    public AlumniDtos.AlumniResponse save(String schoolId, AlumniDtos.AlumniRequest r) {
        if (text(r.fullName()).isBlank() || text(r.studentId()).isBlank() || text(r.batch()).isBlank())
            throw new IllegalArgumentException("Name, student ID and batch are required");
        jdbc.update("""
            INSERT INTO Alumni
              (FullName,PhoneNumber,EmailAddress,Qualification,CurrentStatus,DOB,StudentId,Batch,
               AlumniPhoto,Address,Gender,school_id,status)
            VALUES (?,?,?,?,?,TRY_CONVERT(date,?,23),?,?,?,?,?,?,?,'Active')
            """, text(r.fullName()), text(r.phoneNumber()), text(r.emailAddress()), text(r.qualification()),
            text(r.currentStatus()), text(r.dob()), text(r.studentId()), text(r.batch()),
            text(r.alumniPhoto()), text(r.address()), text(r.gender()), schoolId);
        Integer id=jdbc.queryForObject("SELECT TOP 1 ID FROM Alumni WHERE school_id=? ORDER BY ID DESC",Integer.class,schoolId);
        return new AlumniDtos.AlumniResponse(id,"Alumni registered");
    }
    private static String text(String v){return v==null?"":v.trim();}
}