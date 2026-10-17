package com.edufox.schoolerp.student;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;
import java.util.HashMap;
import java.util.UUID;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class BulkAdmissionRepository {
    private final JdbcTemplate jdbc;

    public List<Map<String, Object>> history(String schoolId, String session) {
        return jdbc.queryForList("""
            SELECT excelId AS id,
                   COUNT(*) AS totalCount,
                   SUM(CASE WHEN status=0 THEN 1 ELSE 0 END) AS uploadedCount,
                   SUM(CASE WHEN ISNULL(onlineRegistrationNO,'')='' OR ISNULL(depositAmount,'')='' OR ISNULL(admno,'')='' OR ISNULL(section,'')='' THEN 1 ELSE 0 END) AS failedCount,
                   MAX(createDate) AS postedDate
            FROM admissionOnlineStudentRegistration_excelRecord
            WHERE schoolId=? AND session=? AND deleteStatus=0
            GROUP BY excelId
            ORDER BY MAX(createDate) DESC
            """, schoolId, session);
    }

    @Transactional
    public Map<String, Object> upload(String schoolId, String staffUid, String session, InputStream input) throws Exception {
        String excelId = UUID.randomUUID().toString().replace("-", "").substring(0, 24);
        DataFormatter formatter = new DataFormatter();
        int count = 0;
        try (Workbook workbook = WorkbookFactory.create(input)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row header = sheet.getRow(0);
            if (header == null) throw new IllegalArgumentException("Excel header row is missing");
            Map<String, Integer> columns = new HashMap<>();
            for (Cell cell : header) columns.put(formatter.formatCellValue(cell).trim().toLowerCase(), cell.getColumnIndex());
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;
                String registration = cell(row, columns, "regisration no", formatter);
                if (registration.isBlank()) registration = cell(row, columns, "registration no", formatter);
                String admission = cell(row, columns, "admission no", formatter);
                if (registration.isBlank() || admission.isBlank()) continue;
                String bank = cell(row, columns, "bank", formatter);
                if (!bank.isBlank()) {
                    jdbc.update("IF NOT EXISTS (SELECT 1 FROM TB_Bank WHERE BankName=?) INSERT INTO TB_Bank(BankName,Display,CreatedBy,CreatedDate) VALUES(?,?,?,GETDATE())", bank, bank, "", staffUid);
                }
                jdbc.update("""
                    INSERT INTO admissionOnlineStudentRegistration_excelRecord
                      (onlineRegistrationNO,admno,section,admissionDate,depositDate,depositAmount,
                       transferToAdvance,payMode,BankName,bank_id,transactionNo,session,schoolId,
                       excelId,status,createDate,uploadedBy,studentCategoryType,deleteStatus)
                    VALUES (?,?,?,?,?,?,?,?,?,(SELECT TOP 1 bank_id FROM TB_Bank WHERE BankName=?),?,?,?, ?,1,GETDATE(),?,?,0)
                    """,
                    registration, admission, cell(row, columns, "section", formatter), cell(row, columns, "admission date", formatter),
                    cell(row, columns, "deposit date", formatter), cell(row, columns, "deposit amount", formatter),
                    cell(row, columns, "transfer to advance", formatter), cell(row, columns, "pay mode", formatter), bank, bank,
                    cell(row, columns, "transaction no", formatter), session, schoolId, excelId,
                    cell(row, columns, "category", formatter), staffUid);
                count++;
            }
        }
        return Map.of("id", excelId, "count", count, "message", "Excel Import To Database successfully");
    }

    public int delete(String schoolId, String session, String excelId) {
        return jdbc.update("UPDATE admissionOnlineStudentRegistration_excelRecord SET deleteStatus=1 WHERE schoolId=? AND session=? AND excelId=? AND status=1", schoolId, session, excelId);
    }

    private static String cell(Row row, Map<String, Integer> columns, String key, DataFormatter formatter) {
        Integer index = columns.get(key.toLowerCase());
        return index == null || row.getCell(index) == null ? "" : formatter.formatCellValue(row.getCell(index)).trim();
    }
}
