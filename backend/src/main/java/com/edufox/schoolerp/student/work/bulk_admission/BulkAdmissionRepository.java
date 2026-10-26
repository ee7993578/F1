package com.edufox.schoolerp.student.work.bulk_admission;
import com.edufox.schoolerp.student.setup.alumni.*;
import com.edufox.schoolerp.student.setup.class_representative.*;
import com.edufox.schoolerp.student.setup.exam_configuration.*;
import com.edufox.schoolerp.student.setup.house_master.*;
import com.edufox.schoolerp.student.setup.registration_open.*;
import com.edufox.schoolerp.student.shared.student_core.*;
import com.edufox.schoolerp.student.update.student_image.*;
import com.edufox.schoolerp.student.update.student_mutation.*;
import com.edufox.schoolerp.student.update.student_update.*;
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
import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;
import java.util.HashMap;
import java.util.UUID;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collection;

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

    public List<Map<String, Object>> excelRows(String schoolId, String session, String excelId) {
        return jdbc.queryForList("""
            SELECT ex.onlineRegistrationNO AS registrationNo, os.regPhone AS regPhone,
                   LTRIM(RTRIM(CONCAT(COALESCE(os.firstName,''),' ',COALESCE(os.midName,''),' ',COALESCE(os.lastName,'')))) AS name,
                   COALESCE(os.fatherName,'') AS fatherName, COALESCE(os.motherName,'') AS motherName,
                   ex.session AS session, ex.admno AS admissionNo, os.admissionClass AS classId,
                   cn.class_name AS className, tc.sec_id AS sectionId, ex.section AS sectionName,
                   ex.admissionDate AS admissionDate, ex.depositDate AS depositDate,
                   ex.depositAmount AS depositAmount,
                   CASE WHEN ex.transferToAdvance='Yes' THEN 1 ELSE 0 END AS advanceStatus,
                   ex.payMode AS payMode, ex.BankName AS bankName, ex.bank_id AS bankId,
                   ex.transactionNo AS transactionNo, ex.studentCategoryType AS category,
                   ex.schoolId AS schoolId, ex.excelId AS excelId
            FROM admissionOnlineStudentRegistration_excelRecord ex
            JOIN onlineStudentRegistration os ON ex.schoolId=os.school_id AND ex.session=os.session
                AND ex.onlineRegistrationNO=os.registrationNO
            JOIN class_name cn ON cn.school_id=ex.schoolId AND cn.id=os.admissionClass
            LEFT JOIN TB_Class tc ON tc.school_id=cn.school_id AND tc.class_id=cn.id AND tc.section=ex.section
            WHERE ex.schoolId=? AND ex.session=? AND ex.excelId=? AND ex.status=1
            ORDER BY ex.id
            """, schoolId, session, excelId);
    }

    /**
     * Direct Java equivalent of the confirm part of the Excel sub-page.  It
     * deliberately writes the same legacy tables and marks the Excel row
     * processed; no legacy procedure is executed.
     */
    @Transactional
    public Map<String, Object> confirm(String schoolId, String staffUid, Map<String, Object> request) {
        String session = text(request.get("session"));
        String excelId = text(request.get("excelId"));
        String onlineNo = text(request.get("registrationNo"));
        String admissionNo = text(request.get("admissionNo"));
        if (session.isBlank() || excelId.isBlank() || onlineNo.isBlank() || admissionNo.isBlank())
            throw new IllegalArgumentException("Session, Excel id, registration number and admission number are required");
        Integer duplicate = jdbc.queryForObject("SELECT COUNT(*) FROM student_other_info WHERE school_id=? AND Registration_NO=?", Integer.class, schoolId, admissionNo);
        if (duplicate != null && duplicate > 0) throw new IllegalArgumentException("Admission number already exists");

        Map<String, Object> online = jdbc.queryForMap("""
            SELECT TOP 1 * FROM onlineStudentRegistration
            WHERE school_id=? AND session=? AND registrationNO=?
              AND AdmissionStatus='Accept' AND formStatus='completed'
            ORDER BY id DESC
            """, schoolId, session, onlineNo);
        Map<String, Object> excel = jdbc.queryForMap("""
            SELECT TOP 1 * FROM admissionOnlineStudentRegistration_excelRecord
            WHERE schoolId=? AND session=? AND excelId=? AND onlineRegistrationNO=? AND admno=? AND status=1
            ORDER BY id
            """, schoolId, session, excelId, onlineNo, admissionNo);
        int classId = number(online.get("admissionClass"));
        int sectionId = sectionId(schoolId, classId, text(excel.get("section")));
        if (sectionId == 0) throw new IllegalArgumentException("Section is required");
        String uid = nextUid(schoolId);
        int stuId = nextStudentId(schoolId);
        String admissionDate = text(excel.get("admissionDate"));
        String paymentDate = text(online.get("paymentDate"));
        String amount = text(excel.get("depositAmount"));
        String payMode = text(excel.get("payMode"));
        String receipt = nextReceipt(schoolId, session);

        jdbc.update("""
            INSERT INTO student_reg(uid,form_no,class_id,Registration_date,created_by,created_date,school_id,session,status,stutype,reg_fee,receipt_no,mop,transaction_no,amount,transaction_date,OnlineRegNo,Admission_date)
            VALUES(?,?,?,?,GETDATE(),?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            """, uid, text(online.get("formNo")), classId, paymentDate, staffUid, schoolId, session,
            "Registered", 1, online.get("paymentAmount"), receipt, payMode,
            text(online.get("order_id")), online.get("paymentAmount"), paymentDate, onlineNo, admissionDate);
        jdbc.update("""
            INSERT INTO student_other_info(uid,school_id,Registration_NO,First_name,Mid_name,Last_name,DOB,Father_name,Mother_name,R_Address,Phone,Mobile,student_E_mail,father_E_mail,Gender,quota,student_aadhar,studentimage)
            VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            """, uid, schoolId, admissionNo, text(online.get("firstName")), text(online.get("midName")),
            text(online.get("lastName")), text(online.get("DOB")), text(online.get("fatherName")),
            text(online.get("motherName")), text(online.get("permanentAddr")), text(online.get("motherPhone")),
            text(online.get("fatherMobile")), text(online.get("studentEmail")), text(online.get("fatherEmail")),
            text(online.get("gender")), text(excel.get("studentCategoryType")), text(online.get("studentAadhar")),
            "/student_images/" + schoolId + "/" + admissionNo + ".jpg");
        jdbc.update("""
            INSERT INTO TB_StudentMaster(uid,stu_id,Registration_NO,House_id,Class_id,sec_id,Admission_date,Registration_date,Withdrawn_date,Withdrawn_reason,status,quota,isnew,activity_id,readmission,school_id,session,stutype,FeeBookNo)
            VALUES(?,?,?,?,?,?,?,?,'','','Registered','0',1,'0',0,?,?,1,'')
            """, uid, stuId, admissionNo, 0, classId, sectionId, admissionDate, paymentDate, schoolId, session);
        if (!amount.isBlank()) {
            jdbc.update("""
                INSERT INTO fee_payment_detail(school_id,financial_year,admno,transaction_no,Bank_id,amount,paymentdate,receiptno,uid,status,deposit_by,deposit_date,transaction_date,reconsillation_status,miscellaneous_fee,mop,fee_type,cheque_usedamount,Settlement_Date,Settlement_status,sub_transaction_no)
                VALUES(?,?,?,?,?,?,?,?,?,'Active',?,GETDATE(),?,'No','',?,0,'',?,1,?)
                """, schoolId, session, admissionNo, text(excel.get("transactionNo")), text(excel.get("bank_id")), amount,
                text(excel.get("depositDate")), receipt, java.util.UUID.randomUUID().toString(), staffUid,
                text(excel.get("depositDate")), payMode, text(excel.get("depositDate")), text(excel.get("transactionNo")));
        }
        jdbc.update("UPDATE onlineStudentRegistration SET AdmissionStatus='Confirm',AdmissionNo=? WHERE school_id=? AND registrationNO=?", admissionNo, schoolId, onlineNo);
        int changed = jdbc.update("UPDATE admissionOnlineStudentRegistration_excelRecord SET status=0 WHERE schoolId=? AND session=? AND onlineRegistrationNO=? AND admno=? AND excelId=? AND status=1", schoolId, session, onlineNo, admissionNo, excelId);
        return Map.of("updated", changed, "message", "Admission completed", "uid", uid, "studentId", stuId);
    }

    private int sectionId(String schoolId, int classId, String section) {
        if (section.isBlank()) return 0;
        Integer id = jdbc.query("SELECT TOP 1 sec_id FROM TB_Class WHERE school_id=? AND class_id=? AND (CAST(sec_id AS varchar(50))=? OR section=?)", rs -> rs.next() ? rs.getInt(1) : 0, schoolId, classId, section, section);
        return id == null ? 0 : id;
    }

    private String nextUid(String schoolId) {
        Integer id = jdbc.queryForObject("SELECT ISNULL(MAX(TRY_CONVERT(int,uid)),0)+1 FROM student_reg WHERE school_id=?", Integer.class, schoolId);
        return String.valueOf(id == null ? 1 : id);
    }

    private int nextStudentId(String schoolId) {
        Integer id = jdbc.queryForObject("SELECT ISNULL(MAX(stu_id),0)+1 FROM TB_StudentMaster WHERE school_id=?", Integer.class, schoolId);
        return id == null ? 1 : id;
    }

    private String nextReceipt(String schoolId, String session) {
        Integer id = jdbc.queryForObject("SELECT ISNULL(MAX(TRY_CONVERT(int,receiptno)),0)+1 FROM fee_payment_detail WHERE school_id=? AND financial_year=?", Integer.class, schoolId, session);
        return String.valueOf(id == null ? 1 : id);
    }

    private static String text(Object value) { return value == null ? "" : String.valueOf(value).trim(); }
    private static int number(Object value) { try { return Integer.parseInt(text(value)); } catch (NumberFormatException ex) { return 0; } }

    private static String cell(Row row, Map<String, Integer> columns, String key, DataFormatter formatter) {
        Integer index = columns.get(key.toLowerCase());
        return index == null || row.getCell(index) == null ? "" : formatter.formatCellValue(row.getCell(index)).trim();
    }
}
