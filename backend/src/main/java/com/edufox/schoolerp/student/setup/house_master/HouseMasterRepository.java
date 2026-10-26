package com.edufox.schoolerp.student.setup.house_master;
import com.edufox.schoolerp.student.setup.alumni.*;
import com.edufox.schoolerp.student.setup.class_representative.*;
import com.edufox.schoolerp.student.setup.exam_configuration.*;
import com.edufox.schoolerp.student.setup.registration_open.*;
import com.edufox.schoolerp.student.shared.student_core.*;
import com.edufox.schoolerp.student.update.student_image.*;
import com.edufox.schoolerp.student.update.student_mutation.*;
import com.edufox.schoolerp.student.update.student_update.*;
import com.edufox.schoolerp.student.work.bulk_admission.*;
import com.edufox.schoolerp.student.work.class_allotment.*;
import com.edufox.schoolerp.student.work.online_registration.*;
import com.edufox.schoolerp.student.work.registration_redirection.*;
import com.edufox.schoolerp.student.work.student_admission.*;
import com.edufox.schoolerp.student.work.student_registration.*;
import com.edufox.schoolerp.student.work.student_work.*;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class HouseMasterRepository {
    private final JdbcTemplate jdbc;

    public List<Map<String,Object>> list(String schoolId) {
        return jdbc.queryForList("""
            SELECT h.id, h.House_id AS houseId, h.H_Name AS houseName, h.H_Incharge_id AS inchargeId,
                   UPPER(REPLACE(LTRIM(RTRIM(COALESCE(f.First_name,'')+' '+COALESCE(f.Mid_name,'')+' '+COALESCE(f.Last_name,''))),'  ',' ')) AS inchargeName,
                   h.Captain_Name AS captain, h.vice_captaion_Name AS viceCaptain,
                   h.House_perfect AS prefect, h.color
            FROM HouseMaster h
            LEFT JOIN tb_facultymaster f ON f.f_id=h.H_Incharge_id AND f.school_id=h.school_id
            WHERE h.school_id=? AND h.status=1 ORDER BY h.id
            """, schoolId);
    }

    @Transactional
    public HouseMasterDtos.HouseResponse save(String schoolId, String staffUid, HouseMasterDtos.HouseRequest request) {
        String name = text(request.houseName());
        if (name.isBlank() || request.inchargeId() == null) throw new IllegalArgumentException("House name and incharge are required");
        if (request.id() == null) {
            if (jdbc.queryForObject("SELECT COUNT(*) FROM HouseMaster WHERE school_id=? AND H_Name=? AND status=1", Integer.class, schoolId, name) > 0)
                throw new IllegalArgumentException("House Already Exists");
            Integer houseId = jdbc.queryForObject("SELECT ISNULL(MAX(House_id),0)+1 FROM HouseMaster WHERE school_id=?", Integer.class, schoolId);
            jdbc.update("""
                INSERT INTO HouseMaster
                  (House_id,H_Name,H_Incharge_id,Captain_Name,vice_captaion_Name,House_perfect,color,created_by,school_id)
                VALUES (?,?,?,?,?,?,?,?,?)
                """, houseId, name, request.inchargeId(), text(request.captain()),
                text(request.viceCaptain()), text(request.prefect()),
                "blue", staffUid, schoolId);
            return new HouseMasterDtos.HouseResponse(houseId, "House saved");
        }
        if (jdbc.queryForObject("SELECT COUNT(*) FROM HouseMaster WHERE school_id=? AND H_Name=? AND House_id<>? AND status=1", Integer.class, schoolId, name, request.id()) > 0)
            throw new IllegalArgumentException("House Already Exists");
        jdbc.update("""
            UPDATE HouseMaster SET H_Name=?, H_Incharge_id=?, Captain_Name=?, vice_captaion_Name=?,
                House_perfect=?, modified_by=?, modified_date=GETDATE()
            WHERE House_id=? AND school_id=?
            """, name, request.inchargeId(), text(request.captain()), text(request.viceCaptain()),
            text(request.prefect()), staffUid, request.id(), schoolId);
        return new HouseMasterDtos.HouseResponse(request.id(), "House updated");
    }

    @Transactional
    public HouseMasterDtos.HouseResponse delete(String schoolId, Integer id) {
        if (jdbc.queryForObject("SELECT COUNT(*) FROM tb_studentmaster WHERE school_id=? AND house_id=?", Integer.class, schoolId, id) > 0)
            throw new IllegalArgumentException("House Already Assigned To Students");
        jdbc.update("UPDATE HouseMaster SET status=0 WHERE House_id=? AND school_id=?", id, schoolId);
        return new HouseMasterDtos.HouseResponse(id, "House deleted");
    }

    private static String text(String v) { return v == null ? "" : v.trim(); }
}
