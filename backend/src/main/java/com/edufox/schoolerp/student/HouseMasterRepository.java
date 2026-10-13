package com.edufox.schoolerp.student;

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
            SELECT id, House_id AS houseId, H_Name AS houseName, H_Incharge_id AS inchargeId,
                   Captain_Name AS captain, vice_captaion_Name AS viceCaptain,
                   House_perfect AS prefect, color
            FROM HouseMaster WHERE school_id=? AND status=1 ORDER BY id
            """, schoolId);
    }

    @Transactional
    public HouseMasterDtos.HouseResponse save(String schoolId, String staffUid, HouseMasterDtos.HouseRequest request) {
        String name = text(request.houseName());
        if (name.isBlank() || request.inchargeId() == null) throw new IllegalArgumentException("House name and incharge are required");
        if (request.id() == null) {
            Integer houseId = jdbc.queryForObject("SELECT ISNULL(MAX(House_id),0)+1 FROM HouseMaster WHERE school_id=?", Integer.class, schoolId);
            jdbc.update("""
                INSERT INTO HouseMaster
                  (House_id,H_Name,H_Incharge_id,Captain_Name,vice_captaion_Name,House_perfect,color,created_by,school_id)
                VALUES (?,?,?,?,?,?,?,?,?)
                """, houseId, name, request.inchargeId(), text(request.captain()),
                text(request.viceCaptain()), text(request.prefect()),
                text(request.color()).isBlank() ? "blue" : text(request.color()), staffUid, schoolId);
            return new HouseMasterDtos.HouseResponse(houseId, "House saved");
        }
        jdbc.update("""
            UPDATE HouseMaster SET H_Name=?, H_Incharge_id=?, Captain_Name=?, vice_captaion_Name=?,
                House_perfect=?, color=?, modified_by=?, modified_date=GETDATE()
            WHERE id=? AND school_id=?
            """, name, request.inchargeId(), text(request.captain()), text(request.viceCaptain()),
            text(request.prefect()), text(request.color()).isBlank() ? "blue" : text(request.color()),
            staffUid, request.id(), schoolId);
        return new HouseMasterDtos.HouseResponse(request.id(), "House updated");
    }

    @Transactional
    public HouseMasterDtos.HouseResponse delete(String schoolId, Integer id) {
        jdbc.update("UPDATE HouseMaster SET status=0 WHERE id=? AND school_id=?", id, schoolId);
        return new HouseMasterDtos.HouseResponse(id, "House deleted");
    }

    private static String text(String v) { return v == null ? "" : v.trim(); }
}