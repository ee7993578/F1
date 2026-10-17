package com.edufox.schoolerp.staff;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.time.LocalDate;

@Repository
@RequiredArgsConstructor
public class StaffRepository {
    private final JdbcTemplate jdbc;

    public Map<String, Object> bootstrap(String schoolId, String session) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("faculty", list(schoolId, session, null, false));
        result.put("withdrawnFaculty", list(schoolId, session, null, true));
        result.put("departments", safe("SELECT row_id AS id, department AS name FROM department WHERE school_id=? OR school_id IS NULL ORDER BY department", schoolId));
        result.put("designations", safe("SELECT desg_id AS id, design_name AS name FROM TB_Desig WHERE school_id=? ORDER BY design_name", schoolId));
        result.put("staffTypes", safe("SELECT StaffTypeId AS id, Name AS name FROM staff_type ORDER BY Name"));
        result.put("mappedDesignations", safe("SELECT DISTINCT ds.DesignationId AS id, ds.DesignationName AS name, dsm.StaffTypeId AS staffTypeId FROM Designation_Staff ds JOIN Designation_Staff_SchoolMapping dsm ON dsm.DesignationId=ds.DesignationId ORDER BY ds.DesignationName"));
        result.put("bloodGroups", safe("SELECT id, blood_group AS name FROM blood_group ORDER BY id"));
        result.put("education", safe("SELECT id, Education AS name FROM Education_Quli ORDER BY id"));
        result.put("states", safe("SELECT state_id AS id, state_name AS name FROM state ORDER BY state_name"));
        result.put("districts", safe("SELECT city_id AS id, city_name AS name, state_id AS stateId FROM districts ORDER BY city_name"));
        result.put("banks", safe("SELECT bank_id AS id, bankname AS name, display FROM TB_Bank ORDER BY bankname"));
        result.put("subjects", safe("SELECT id, Subject AS name FROM tb_subjectmaster WHERE school_id=? AND status=1 ORDER BY Subject", schoolId));
        result.put("sessions", safe("SELECT session, status FROM TB_Session WHERE school_id=? ORDER BY id DESC", schoolId));
        result.put("classes", safe("SELECT id, class_name AS name FROM class_name WHERE school_id=? ORDER BY id", schoolId));
        result.put("nextFacultyId", nextFacultyId(schoolId));
        return result;
    }

    public List<Map<String, Object>> list(String schoolId, String session, String search, boolean withdrawn) {
        StringBuilder sql = new StringBuilder(selectFaculty());
        List<Object> args = new ArrayList<>();
        args.add(schoolId);
        // Legacy ASPX pages always filter against BusinessLogic.currentSession().
        // Do the same when the React page does not explicitly send a session.
        String currentSession = (session == null || session.isBlank()) ? effectiveSession(schoolId, null) : session;
        if (withdrawn) {
            sql.append(" AND EXISTS (SELECT 1 FROM tb_faculty_withdrawn fw WHERE fw.school_id=f.school_id AND fw.f_id=f.f_id AND LOWER(fw.status)='withdrawn'");
        } else {
            // faculty-infosheet.aspx is backed by the current-session Registered rows.
            sql.append(" AND EXISTS (SELECT 1 FROM tb_faculty_withdrawn fw WHERE fw.school_id=f.school_id AND fw.f_id=f.f_id AND LOWER(fw.status)='registered'");
        }
        sql.append(" AND fw.session=?");
        args.add(currentSession);
        sql.append(")");
        if (search != null && !search.isBlank()) {
            String q = "%" + search.trim() + "%";
            sql.append(" AND (f.faculty_id LIKE ? OR f.staff_u_id LIKE ? OR f.first_name LIKE ? OR f.mid_name LIKE ? OR f.last_name LIKE ? OR f.e_mail LIKE ? OR f.mobile LIKE ?)");
            Collections.addAll(args, q, q, q, q, q, q, q);
        }
        sql.append(" ORDER BY f.first_name, f.last_name");
        return jdbc.queryForList(sql.toString(), args.toArray());
    }

    public Map<String, Object> find(String schoolId, String id) {
        String sql = selectFaculty() + " AND (CAST(f.f_id AS varchar(100))=? OR f.faculty_id=? OR f.staff_u_id=?)";
        List<Map<String, Object>> rows = jdbc.queryForList(sql, schoolId, id, id, id);
        return rows.isEmpty() ? Map.of() : rows.get(0);
    }

    @Transactional
    public StaffDtos.MutationResponse register(String schoolId, String staffUid, String session, Map<String, Object> body) {
        session = effectiveSession(schoolId, session);
        String facultyId = text(body.get("facultyId"));
        if (facultyId.isBlank()) facultyId = nextFacultyId(schoolId);
        String first = required(body, "firstName", "First name is required");
        String generatedUid = text(body.get("staffUid"));
        if (generatedUid.isBlank()) generatedUid = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase(Locale.ROOT);
        Integer duplicate = jdbc.queryForObject("SELECT COUNT(*) FROM tb_facultymaster WHERE school_id=? AND faculty_id=?", Integer.class, schoolId, facultyId);
        if (duplicate != null && duplicate > 0) throw new IllegalArgumentException("Faculty ID already exists");
        String password = text(body.get("password"));
        if (password.isBlank()) password = randomPassword(first);
        Integer fId = nextFid(schoolId);
        jdbc.update("""
            INSERT INTO tb_facultymaster
            (f_id,staff_u_id,faculty_id,first_name,mid_name,last_name,dob,FH_name,H_name,R_Address,Pin,Phone,Mobile,E_mail,
               M_status,District_id,City,State_id,Gender,Joining_date,Blood_gp,BQualification,HQualification,Present_Address,
               Anniversary,Experience,Department,HOccupation,created_by,school_id,session,faculty_initial,addharcard_no,
               pancard_no,bank_name,acc_no,ifsc_code,branch,designation,image_path,password,status)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            """, fId, generatedUid, facultyId, first, text(body.get("middleName")), text(body.get("lastName")),
            value(body,"dob"), text(body.get("fatherName")), text(body.get("husbandName")), text(body.get("permanentAddress")), text(body.get("pin")),
            text(body.get("telephone")), text(body.get("mobile")), text(body.get("email")), text(body.get("maritalStatus")),
            value(body,"district"), text(body.get("city")), value(body,"state"), text(body.get("gender")), value(body,"doj"),
            value(body,"bloodGroup"), text(body.get("graduation")), text(body.get("education")), text(body.get("presentAddress")),
            text(body.get("anniversary")), text(body.get("experience")), value(body,"department"), value(body,"designation"),
            staffUid, schoolId, session, text(body.get("salutation")), text(body.get("aadhar")), text(body.get("pan")),
            text(body.get("bankName")), text(body.get("accountNo")), text(body.get("ifsc")), text(body.get("branch")),
            value(body,"designation"), text(body.get("facultyPhoto")), password, 1);
        registerSession(schoolId, fId, generatedUid, session, staffUid);
        saveSubjects(schoolId, fId, body.get("subjects"));
        return new StaffDtos.MutationResponse(String.valueOf(fId), "Faculty registered successfully");
    }

    @Transactional
    public StaffDtos.MutationResponse update(String schoolId, String id, Map<String, Object> body, String modifiedBy) {
        Integer fId = findFid(schoolId, id);
        if (fId == null) throw new IllegalArgumentException("Faculty record not found");
        List<String> set = new ArrayList<>(); List<Object> args = new ArrayList<>();
        field(set,args,"faculty_id",body.get("facultyId")); field(set,args,"first_name",body.get("firstName")); field(set,args,"mid_name",body.get("middleName"));
        field(set,args,"last_name",body.get("lastName")); field(set,args,"FH_name",body.get("fatherName")); field(set,args,"H_name",body.get("husbandName"));
        field(set,args,"dob",body.get("dob")); field(set,args,"gender",body.get("gender")); field(set,args,"blood_gp",body.get("bloodGroup"));
        field(set,args,"BQualification",body.get("graduation")); field(set,args,"HQualification",body.get("education")); field(set,args,"Experience",body.get("experience"));
        field(set,args,"Department",body.get("department")); field(set,args,"HOccupation",body.get("designation")); field(set,args,"Joining_date",body.get("doj"));
        field(set,args,"District_id",body.get("district")); field(set,args,"City",body.get("city")); field(set,args,"State_id",body.get("state"));
        field(set,args,"Present_Address",body.get("presentAddress")); field(set,args,"R_Address",body.get("permanentAddress")); field(set,args,"Pin",body.get("pin"));
        field(set,args,"Phone",body.get("telephone")); field(set,args,"Mobile",body.get("mobile")); field(set,args,"E_mail",body.get("email"));
        field(set,args,"addharcard_no",body.get("aadhar")); field(set,args,"pancard_no",body.get("pan")); field(set,args,"bank_name",body.get("bankName")); field(set,args,"acc_no",body.get("accountNo"));
        field(set,args,"ifsc_code",body.get("ifsc")); field(set,args,"branch",body.get("branch")); field(set,args,"image_path",body.get("facultyPhoto"));
        if (!set.isEmpty()) { args.add(fId); args.add(schoolId); jdbc.update("UPDATE tb_facultymaster SET " + String.join(",",set) + " WHERE f_id=? AND school_id=?", args.toArray()); }
        saveSubjects(schoolId, fId, body.get("subjects"));
        return new StaffDtos.MutationResponse(String.valueOf(fId), "Faculty information updated");
    }

    @Transactional
    public StaffDtos.MutationResponse withdraw(String schoolId, String id, String session, String reason, String by) {
        session = effectiveSession(schoolId, session);
        Integer fId = findFid(schoolId, id); if (fId == null) throw new IllegalArgumentException("Faculty record not found");
        String uid = jdbc.queryForObject("SELECT staff_u_id FROM tb_facultymaster WHERE f_id=? AND school_id=?", String.class, fId, schoolId);
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM tb_faculty_withdrawn WHERE f_id=? AND school_id=? AND session=?", Integer.class, fId, schoolId, session);
        if (count != null && count > 0) jdbc.update("UPDATE tb_faculty_withdrawn SET status='Withdrawn',remark=?,modified_by=?,modified_date=GETDATE() WHERE f_id=? AND school_id=? AND session=?", reason, by, fId, schoolId, session);
        else jdbc.update("INSERT INTO tb_faculty_withdrawn(f_id,staff_u_id,school_id,session,status,remark,created_by,created_date) VALUES(?,?,?,?,?,?,?,GETDATE())", fId, uid, schoolId, session, "Withdrawn", reason, by);
        return new StaffDtos.MutationResponse(String.valueOf(fId), "Faculty withdrawn successfully");
    }

    @Transactional
    public StaffDtos.MutationResponse reregister(String schoolId, String id, String session, String by) {
        session = effectiveSession(schoolId, session);
        Integer fId = findFid(schoolId, id); if (fId == null) throw new IllegalArgumentException("Faculty record not found");
        String uid = jdbc.queryForObject("SELECT staff_u_id FROM tb_facultymaster WHERE f_id=? AND school_id=?", String.class, fId, schoolId);
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM tb_faculty_withdrawn WHERE f_id=? AND school_id=? AND session=?", Integer.class, fId, schoolId, session);
        if (count != null && count > 0) jdbc.update("UPDATE tb_faculty_withdrawn SET status='Registered',remark=NULL,modified_by=?,modified_date=GETDATE() WHERE f_id=? AND school_id=? AND session=?", by, fId, schoolId, session);
        else jdbc.update("INSERT INTO tb_faculty_withdrawn(f_id,staff_u_id,school_id,session,status,created_by,created_date) VALUES(?,?,?,?,?,?,GETDATE())", fId, uid, schoolId, session, "Registered", by);
        return new StaffDtos.MutationResponse(String.valueOf(fId), "Faculty registered successfully");
    }

    public List<Map<String,Object>> classTeacherReport(String schoolId, String session) {
        return jdbc.queryForList("""
            SELECT ct.section_id AS sectionId, COALESCE(cn.class_name,'') AS className, COALESCE(c.section,'') AS sectionName,
                   ct.f_id AS facultyId, ct.f_id_co AS coFacultyId,
                   CONCAT(COALESCE(f.first_name,''),' ',COALESCE(f.mid_name,''),' ',COALESCE(f.last_name,'')) AS facultyName,
                   f.staff_u_id AS userId, f.password AS password
            FROM TB_ClassTeacher ct LEFT JOIN TB_Class c ON c.sec_id=ct.section_id AND c.school_id=ct.school_id
            LEFT JOIN class_name cn ON cn.id=c.class_id AND cn.school_id=c.school_id
            LEFT JOIN tb_facultymaster f ON f.f_id=ct.f_id AND f.school_id=ct.school_id
            WHERE ct.school_id=? AND ct.session=? ORDER BY c.class_id,c.section
            """, schoolId, session);
    }

    /** Direct SQL equivalent of bind_class_teacher, without calling the legacy procedure. */
    public Map<String,Object> classTeacherSetup(String schoolId, String session) {
        List<Map<String,Object>> rows = jdbc.queryForList("""
            SELECT c.sec_id AS sectionId, c.class_id AS classId,
                   COALESCE(cn.class_name,'') AS className, COALESCE(c.section,'') AS sectionName,
                   COALESCE(ct.f_id,0) AS facultyId, COALESCE(ct.f_id_co,0) AS coFacultyId
            FROM TB_Class c
            LEFT JOIN class_name cn ON cn.id=c.class_id AND cn.school_id=c.school_id
            LEFT JOIN TB_ClassTeacher ct ON ct.section_id=c.sec_id AND ct.school_id=c.school_id AND ct.session=?
            WHERE c.school_id=? AND c.sec_id<>0
            ORDER BY c.class_id,c.sec_id
            """, session, schoolId);
        List<Map<String,Object>> teachers = jdbc.queryForList("""
            SELECT f.f_id AS id,
                   LTRIM(RTRIM(CONCAT(COALESCE(f.first_name,''),' ',COALESCE(f.mid_name,''),' ',COALESCE(f.last_name,'')))) AS name
            FROM tb_facultymaster f
            WHERE f.school_id=? AND f.f_id IN
              (SELECT w.f_id FROM tb_faculty_withdrawn w WHERE w.school_id=? AND w.session=? AND LOWER(w.status)='registered')
            ORDER BY name
            """, schoolId, schoolId, session);
        return Map.of("rows", rows, "teachers", teachers, "session", session);
    }

    public List<Map<String,Object>> activeFaculty(String schoolId, String session) { return list(schoolId,session,null,false); }

    /**
     * The legacy report is backed by the job-application table, but its name has
     * differed between deployments. Resolve it from SQL Server metadata instead
     * of hard-coding a table or calling the old stored procedure.
     */
    public Map<String, Object> jobApplicationOptions(String schoolId, String post) {
        Map<String, String> columns = jobApplicationColumns();
        if (columns.isEmpty()) return Map.of("posts", List.of(), "subjects", List.of(), "statuses", List.of());
        String table = jobTable(columns);
        String postColumn = firstColumn(columns, "appliedpost", "applied_post", "post");
        String subjectColumn = firstColumn(columns, "department", "subject", "subjectname", "subjectvalue", "appliedsubject");
        List<Object> postArgs = new ArrayList<>();
        String postSql = "SELECT DISTINCT CONVERT(varchar(255)," + identifier(postColumn) + ") AS value FROM " + table
                + " WHERE " + identifier(columns.get("school_id")) + "=? AND " + identifier(postColumn) + " IS NOT NULL";
        postArgs.add(schoolId);
        postSql += " ORDER BY value";
        List<Map<String, Object>> posts = jdbc.queryForList(postSql, postArgs.toArray());

        List<Map<String, Object>> subjects = List.of();
        if (subjectColumn != null) {
            String subjectSql = "SELECT DISTINCT CONVERT(varchar(255)," + identifier(subjectColumn) + ") AS value FROM " + table
                    + " WHERE " + identifier(columns.get("school_id")) + "=? AND " + identifier(subjectColumn) + " IS NOT NULL";
            List<Object> args = new ArrayList<>(); args.add(schoolId);
            if (post != null && !post.isBlank()) { subjectSql += " AND " + identifier(postColumn) + "=?"; args.add(post); }
            subjectSql += " ORDER BY value";
            subjects = jdbc.queryForList(subjectSql, args.toArray());
        }
        List<Map<String, Object>> statuses = List.of();
        String statusColumn = firstColumn(columns, "applicationstatus", "application_status", "status");
        if (statusColumn != null) statuses = jdbc.queryForList("SELECT DISTINCT CONVERT(varchar(100)," + identifier(statusColumn) + ") AS value FROM " + table + " WHERE " + identifier(columns.get("school_id")) + "=? AND " + identifier(statusColumn) + " IS NOT NULL ORDER BY value", schoolId);
        List<Map<String,Object>> months = safe("SELECT month_id AS value, month_name AS label FROM tb_month ORDER BY month_id");
        return Map.of("posts", posts, "subjects", subjects, "statuses", statuses, "months", months);
    }

    public List<Map<String, Object>> jobApplications(String schoolId, String post, String subject, String month, String status) {
        Map<String, String> columns = jobApplicationColumns();
        if (columns.isEmpty()) return List.of();
        String table = jobTable(columns);
        String schoolColumn = columns.get("school_id");
        String postColumn = firstColumn(columns, "appliedpost", "applied_post", "post");
        String subjectColumn = firstColumn(columns, "department", "subject", "subjectname", "subjectvalue", "appliedsubject");
        String statusColumn = firstColumn(columns, "applicationstatus", "application_status", "status");
        String dateColumn = firstColumn(columns, "dateofapplication", "applicationdate", "dateofapply", "createddate", "created_at");

        StringBuilder sql = new StringBuilder("SELECT ");
        sql.append(expression(columns, "applicationid", "application_id", "applicationnumber")).append(" AS ApplicationID,");
        sql.append(expression(columns, "registrationno", "registration_no", "registrationnumber")).append(" AS registrationno,");
        sql.append(fullNameExpression(columns)).append(" AS FullName,");
        sql.append(expression(columns, "mobilenumber", "mobile", "mobileno")).append(" AS MobileNumber,");
        sql.append(expression(columns, "email", "emailid", "e_mail")).append(" AS Email,");
        sql.append(expression(columns, "city")).append(" AS City,");
        sql.append(expression(columns, "state", "statename")).append(" AS State,");
        sql.append(expression(columns, "appliedpost", "applied_post", "post")).append(" AS AppliedPost,");
        sql.append(expression(columns, "department", "subject", "subjectname", "subjectvalue", "appliedsubject")).append(" AS Department,");
        sql.append(expression(columns, "applicationstatus", "application_status", "status")).append(" AS ApplicationStatus,");
        sql.append(dateExpression(columns, "dateofapplication", "applicationdate", "dateofapply", "createddate", "created_at")).append(" AS DateOfApplication,");
        sql.append(dateExpression(columns, "feepaymentdate", "fee_payment_date", "paymentdate")).append(" AS FeePaymentDate,");
        sql.append(dateExpression(columns, "settlementdate", "settlement_date", "settleddate")).append(" AS settlementDate,");
        sql.append(expression(columns, "registrationfees", "registration_fees", "feeamount")).append(" AS RegistrationFees,");
        sql.append(expression(columns, "orderid", "order_id")).append(" AS orderId,");
        sql.append(expression(columns, "feetransactionid", "fee_transaction_id", "transactionid", "transaction_no")).append(" AS FeeTransactionID,");
        sql.append(expression(columns, "resumepath", "resume_path", "resume")).append(" AS ResumePath");
        sql.append(" FROM ").append(table).append(" WHERE ").append(identifier(schoolColumn)).append("=?");
        List<Object> args = new ArrayList<>(); args.add(schoolId);
        if (post != null && !post.isBlank() && postColumn != null) { sql.append(" AND ").append(identifier(postColumn)).append("=?"); args.add(post); }
        if (subject != null && !subject.isBlank() && subjectColumn != null) { sql.append(" AND ").append(identifier(subjectColumn)).append("=?"); args.add(subject); }
        if (status != null && !status.isBlank() && statusColumn != null) { sql.append(" AND ").append(identifier(statusColumn)).append("=?"); args.add(status); }
        Integer monthNumber = monthNumber(month);
        if (monthNumber != null && dateColumn != null) { sql.append(" AND MONTH(TRY_CONVERT(date,").append(identifier(dateColumn)).append("))=?"); args.add(monthNumber); }
        if (dateColumn != null) sql.append(" ORDER BY ").append(identifier(dateColumn)).append(" DESC");
        return jdbc.queryForList(sql.toString(), args.toArray());
    }

    @Transactional
    public StaffDtos.MutationResponse credentials(String schoolId, String id, Map<String,Object> body) {
        Integer fId=findFid(schoolId,id); if(fId==null) throw new IllegalArgumentException("Faculty record not found");
        jdbc.update("UPDATE tb_facultymaster SET E_mail=?,Password=? WHERE f_id=? AND school_id=?", text(body.get("userEmail")), text(body.get("password")), fId, schoolId);
        return new StaffDtos.MutationResponse(String.valueOf(fId),"Faculty credentials updated");
    }

    @Transactional
    public StaffDtos.MutationResponse saveClassTeachers(String schoolId, String by, StaffDtos.ClassTeacherRequest request) {
        for (StaffDtos.ClassTeacherAssignment a : request.assignments()) {
            Integer count=jdbc.queryForObject("SELECT COUNT(*) FROM TB_ClassTeacher WHERE school_id=? AND session=? AND section_id=?",Integer.class,schoolId,request.session(),a.sectionId());
            if(count!=null && count>0) jdbc.update("UPDATE TB_ClassTeacher SET f_id=?,f_id_co=?,modified_by=?,modified_date=GETDATE() WHERE school_id=? AND session=? AND section_id=?",a.facultyId(),a.coFacultyId(),by,schoolId,request.session(),a.sectionId());
            else jdbc.update("INSERT INTO TB_ClassTeacher(section_id,f_id,f_id_co,school_id,session,created_by,created_date) VALUES(?,?,?,?,?,?,GETDATE())",a.sectionId(),a.facultyId(),a.coFacultyId(),schoolId,request.session(),by);
        }
        return new StaffDtos.MutationResponse(request.session(),"Class teachers saved");
    }

    public List<Map<String,Object>> jobConfig(String schoolId,String session) {
        String sql = "SELECT RowID AS id, session, CONVERT(varchar(10),startDate,23) AS startDate, CONVERT(varchar(10),endDate,23) AS endDate, feeAmount, CONVERT(varchar(10),status) AS status FROM OpenCloseJobApplicationForm WHERE school_id=?";
        List<Object> args = new ArrayList<>(); args.add(schoolId);
        if(session!=null&&!session.isBlank()){sql += " AND session=?";args.add(session);}
        sql += " ORDER BY session DESC, RowID DESC";
        return jdbc.queryForList(sql,args.toArray());
    }

    public Map<String,Object> jobConfigOptions(String schoolId) {
        return Map.of("sessions", safe("SELECT session AS value, session AS label, status FROM TB_Session WHERE school_id=? ORDER BY id DESC", schoolId));
    }

    @Transactional
    public StaffDtos.MutationResponse saveJobConfig(String schoolId,String by,Map<String,Object> body) {
        String session=required(body,"session","Session is required"), start=required(body,"startDate","Start date is required"), end=required(body,"endDate","End date is required");
        if(end.compareTo(start)<=0) throw new IllegalArgumentException("End date must be after start date");
        if(LocalDate.parse(start).isBefore(LocalDate.now()) || LocalDate.parse(end).isBefore(LocalDate.now())) throw new IllegalArgumentException("Start/End date should be today or a future date");
        boolean active=active(body.get("status"));
        if(active && (jdbc.queryForObject("SELECT COUNT(*) FROM OpenCloseJobApplicationForm WHERE school_id=? AND status=1",Integer.class,schoolId) > 0)) throw new IllegalArgumentException("Cannot insert. Another record is already Active!");
        if(jdbc.queryForObject("SELECT COUNT(*) FROM OpenCloseJobApplicationForm WHERE school_id=? AND session=? AND startDate=? AND endDate=?",Integer.class,schoolId,session,start,end) > 0) throw new IllegalArgumentException("These dates have already been entered");
        jdbc.update("INSERT INTO OpenCloseJobApplicationForm(school_id,session,startDate,endDate,feeAmount,status,createdAt,updatedAt,updatedBy,createdBy) VALUES(?,?,?,?,?,?,GETDATE(),GETDATE(),?,?)",
            schoolId,session,start,end,number(body.get("feeAmount")),active?1:0,by,schoolId);
        Object id=jdbc.queryForObject("SELECT TOP 1 RowID FROM OpenCloseJobApplicationForm WHERE school_id=? ORDER BY RowID DESC",Object.class,schoolId);
        return new StaffDtos.MutationResponse(String.valueOf(id),"Registration opening added successfully");
    }

    @Transactional
    public StaffDtos.MutationResponse updateJobConfig(String schoolId,String by,String id,Map<String,Object> body) {
        String start=required(body,"startDate","Start date is required"), end=required(body,"endDate","End date is required");
        if(end.compareTo(start)<=0) throw new IllegalArgumentException("End date must be after start date");
        boolean active=active(body.get("status"));
        if(active) jdbc.update("UPDATE OpenCloseJobApplicationForm SET status=0,updatedAt=GETDATE(),updatedBy=? WHERE school_id=? AND status=1 AND RowID<>?",by,schoolId,id);
        int count=jdbc.update("UPDATE OpenCloseJobApplicationForm SET startDate=?,endDate=?,feeAmount=?,status=?,updatedAt=GETDATE(),updatedBy=? WHERE school_id=? AND RowID=?",start,end,number(body.get("feeAmount")),active?1:0,by,schoolId,id);
        if(count==0) throw new IllegalArgumentException("Registration configuration not found");
        return new StaffDtos.MutationResponse(id,"Registration opening updated successfully");
    }

    private String selectFaculty() { return """
        SELECT f.f_id AS id,f.f_id AS f_id,f.staff_u_id AS staffUid,f.faculty_id AS facultyId,
          f.first_name AS firstName,f.mid_name AS middleName,f.last_name AS lastName,
          CONCAT(COALESCE(f.first_name,''),' ',COALESCE(f.mid_name,''),' ',COALESCE(f.last_name,'')) AS name,
          f.FH_name AS fatherName,f.H_name AS husbandName,f.dob,f.gender,f.blood_gp AS bloodGroup,
          f.BQualification AS graduation,f.HQualification AS education,f.Experience AS experience,
          f.Department AS department,f.HOccupation AS designation,f.Joining_date AS doj,f.District_id AS district,
          f.City AS city,f.State_id AS state,f.Present_Address AS presentAddress,f.R_Address AS permanentAddress,
          f.Pin AS pin,f.Phone AS telephone,f.Mobile AS mobile,f.E_mail AS email,f.addharcard_no AS aadhar,
          f.pancard_no AS pan,f.bank_name AS bankName,f.acc_no AS accountNo,f.ifsc_code AS ifsc,f.branch,
          (SELECT STRING_AGG(CONVERT(varchar(max),sm.Subject), ', ') FROM TB_Faculty_Subject fs JOIN tb_subjectmaster sm ON sm.id=fs.subject_id AND sm.school_id=fs.school_id WHERE fs.f_id=f.f_id AND fs.school_id=f.school_id) AS subject,
          f.image_path AS facultyPhoto,f.password,f.status,f.priority,f.anniversary,f.faculty_initial AS salutation,
          (SELECT TOP 1 fw.status FROM tb_faculty_withdrawn fw WHERE fw.f_id=f.f_id AND fw.school_id=f.school_id ORDER BY fw.session DESC) AS registrationStatus,
          (SELECT TOP 1 fw.remark FROM tb_faculty_withdrawn fw WHERE fw.f_id=f.f_id AND fw.school_id=f.school_id ORDER BY fw.session DESC) AS remark
        FROM tb_facultymaster f WHERE f.school_id=?"""; }
    private void registerSession(String schoolId,Integer fId,String uid,String session,String by){
        if(session==null||session.isBlank()) return;
        jdbc.update("INSERT INTO tb_faculty_withdrawn(f_id,staff_u_id,school_id,session,status,created_by,created_date) VALUES(?,?,?,?,?,?,GETDATE())",fId,uid,schoolId,session,"Registered",by);
    }
    private void saveSubjects(String schoolId,Integer fId,Object raw){ if(!(raw instanceof Collection<?> c)) return; jdbc.update("DELETE FROM TB_Faculty_Subject WHERE f_id=? AND school_id=?",fId,schoolId); for(Object x:c) jdbc.update("INSERT INTO TB_Faculty_Subject(f_id,school_id,subject_id) VALUES(?,?,?)",fId,schoolId,text(x)); }
    private Integer findFid(String schoolId,String id){ try{return jdbc.queryForObject("SELECT TOP 1 f_id FROM tb_facultymaster WHERE school_id=? AND (CAST(f_id AS varchar(100))=? OR faculty_id=? OR staff_u_id=?)",Integer.class,schoolId,id,id,id);}catch(EmptyResultDataAccessException e){return null;} }
    private Integer nextFid(String schoolId){Integer n=jdbc.queryForObject("SELECT ISNULL(MAX(f_id),0)+1 FROM tb_facultymaster WHERE school_id=?",Integer.class,schoolId);return n==null?1:n;}
    private String nextFacultyId(String schoolId){Object n=jdbc.queryForObject("SELECT ISNULL(MAX(TRY_CONVERT(int,faculty_id)),0)+1 FROM tb_facultymaster WHERE school_id=?",Object.class,schoolId);return n==null?"1":String.valueOf(n);}
    private String effectiveSession(String schoolId,String session){
        if(session!=null&&!session.isBlank()) return session;
        try { return jdbc.queryForObject("SELECT TOP 1 session FROM TB_Session WHERE school_id=? AND (status=1 OR LOWER(CONVERT(varchar(20),status)) IN ('active','current','default')) ORDER BY id DESC",String.class,schoolId); }
        catch(Exception ignored){ return jdbc.queryForObject("SELECT TOP 1 session FROM TB_Session WHERE school_id=? ORDER BY id DESC",String.class,schoolId); }
    }
    private List<Map<String,Object>> safe(String sql,Object...args){try{return jdbc.queryForList(sql,args);}catch(Exception e){return List.of();}}
    private static void field(List<String>s,List<Object>a,String col,Object v){if(v!=null&& !String.valueOf(v).isBlank()){s.add(col+"=?");a.add(v);}}
    private static String required(Map<String,Object>b,String k,String msg){String v=text(b.get(k));if(v.isBlank())throw new IllegalArgumentException(msg);return v;}
    private static String text(Object o){return o==null?"":String.valueOf(o).trim();}
    private static Object value(Map<String,Object>b,String k){String v=text(b.get(k));return v.isBlank()?null:v;}
    private static boolean active(Object value){String v=text(value);return "1".equals(v)||"true".equalsIgnoreCase(v)||"active".equalsIgnoreCase(v);}
    private static int number(Object value){try{return Integer.parseInt(text(value).isBlank()?"0":text(value));}catch(NumberFormatException e){throw new IllegalArgumentException("Fee amount must be numeric");}}
    private static String randomPassword(String name){return (name.length()>0?name.substring(0,Math.min(4,name.length())):"User")+"@1234";}

    private Map<String, String> jobApplicationColumns() {
        List<Map<String, Object>> metadata = jdbc.queryForList("""
            SELECT c.TABLE_SCHEMA AS tableSchema,c.TABLE_NAME AS tableName,c.COLUMN_NAME AS columnName
            FROM INFORMATION_SCHEMA.COLUMNS c
            JOIN INFORMATION_SCHEMA.TABLES t ON t.TABLE_SCHEMA=c.TABLE_SCHEMA AND t.TABLE_NAME=c.TABLE_NAME
            WHERE t.TABLE_TYPE='BASE TABLE'
              AND LOWER(c.COLUMN_NAME) IN ('school_id','applicationid','application_id','registrationno','registration_no',
                'fullname','full_name','name','mobilenumber','mobile','mobileno','email','emailid','e_mail','city','state',
                'statename','appliedpost','applied_post','post','department','subject','subjectname','subjectvalue',
                'appliedsubject','applicationstatus','application_status','status','dateofapplication','applicationdate',
                'dateofapply','createddate','created_at','feepaymentdate','fee_payment_date','paymentdate','settlementdate',
                'settlement_date','settleddate','registrationfees','registration_fees','feeamount','orderid','order_id',
                'feetransactionid','fee_transaction_id','transactionid','transaction_no','resumepath','resume_path','resume',
                'first_name','middle_name','mid_name','last_name')
            """);
        Map<String, Map<String, String>> byTable = new LinkedHashMap<>();
        for (Map<String, Object> row : metadata) {
            String schema = text(row.get("tableSchema")), table = text(row.get("tableName")), column = text(row.get("columnName"));
            if (!schema.isBlank() && !table.isBlank()) byTable.computeIfAbsent(schema + "." + table, ignored -> new LinkedHashMap<>()).putIfAbsent(column.toLowerCase(Locale.ROOT), column);
        }
        Map<String, String> selected = byTable.entrySet().stream()
            .filter(e -> e.getValue().containsKey("school_id") && (e.getValue().containsKey("applicationid") || e.getValue().containsKey("application_id")))
            .max(Comparator.comparingInt(e -> jobTableScore(e.getValue())))
            .map(Map.Entry::getValue).orElse(Map.of());
        if (!selected.isEmpty()) {
            for (Map.Entry<String, Map<String, String>> entry : byTable.entrySet()) {
                if (entry.getValue() == selected) {
                    String[] object = entry.getKey().split("\\.", 2);
                    selected.put("__schema", object[0]); selected.put("__table", object[1]);
                    break;
                }
            }
        }
        return selected;
    }

    private int jobTableScore(Map<String, String> columns) {
        int score = 0;
        if (firstColumn(columns,"appliedpost","applied_post","post") != null) score += 10;
        if (firstColumn(columns,"dateofapplication","applicationdate","dateofapply") != null) score += 5;
        if (firstColumn(columns,"fullname","full_name","name") != null) score += 3;
        if (firstColumn(columns,"registrationno","registration_no","registrationnumber") != null) score += 2;
        return score;
    }

    private String jobTable(Map<String, String> columns) {
        return identifier(columns.getOrDefault("__schema", "dbo")) + "." + identifier(columns.getOrDefault("__table", "FacultyJobApplication"));
    }

    private String firstColumn(Map<String, String> columns, String... names) { for (String name : names) if (columns.containsKey(name)) return columns.get(name); return null; }
    private String expression(Map<String, String> columns, String... names) { String column = firstColumn(columns, names); return column == null ? "CAST(NULL AS varchar(255))" : "CONVERT(varchar(255)," + identifier(column) + ")"; }
    private String dateExpression(Map<String, String> columns, String... names) { String column = firstColumn(columns, names); return column == null ? "CAST(NULL AS varchar(30))" : "CONVERT(varchar(11),TRY_CONVERT(date," + identifier(column) + "),106)"; }
    private String fullNameExpression(Map<String, String> columns) {
        String full = firstColumn(columns,"fullname","full_name","name"); if (full != null) return expression(columns, full.toLowerCase(Locale.ROOT));
        String first = firstColumn(columns,"first_name"), middle = firstColumn(columns,"middle_name","mid_name"), last = firstColumn(columns,"last_name");
        if (first != null || last != null) return "LTRIM(RTRIM(CONCAT(COALESCE(" + (first == null ? "''" : identifier(first)) + ",' '),COALESCE(" + (middle == null ? "''" : identifier(middle)) + ",' '),COALESCE(" + (last == null ? "''" : identifier(last)) + ",' ')))";
        return "CAST(NULL AS varchar(255))";
    }
    private String identifier(String value) { return "[" + value.replace("]", "]]" ) + "]"; }
    private Integer monthNumber(String month) { if (month == null || month.isBlank()) return null; try { int n = Integer.parseInt(month); return n >= 1 && n <= 12 ? n : null; } catch (NumberFormatException ignored) { List<String> names = List.of("january","february","march","april","may","june","july","august","september","october","november","december"); int n = names.indexOf(month.toLowerCase(Locale.ROOT)); return n < 0 ? null : n + 1; } }
}
