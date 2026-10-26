package com.edufox.schoolerp.website.shared.website;

import com.edufox.schoolerp.config.DatabaseWriteGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class WebsiteService {
    private final JdbcTemplate jdbc;
    private final WebsiteRepository repo;
    private final DatabaseWriteGuard writeGuard;
    private final Path storageRoot = Paths.get(System.getProperty("user.dir"), "uploads").toAbsolutePath().normalize();

    public Map<String, Object> bootstrap(String school) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("sessions", repo.sessions(school));
        out.put("classes", repo.classes(school));
        out.put("sections", repo.sections(school));
        out.put("schoolId", school);
        return out;
    }

    public List<Map<String, Object>> students(String school, String session, String sections) { return repo.students(school, session, sections); }
    public List<Map<String, Object>> exams(String school, String session, String classId) { return repo.examOptions(school, session, classId); }

    public List<Map<String, Object>> notices(String school, String session, String type) {
        String sql = "SELECT DISTINCT noticetitle_id AS id,type,Description AS description,notice_date AS noticeDate,file_path AS filePath,category FROM school_notice WHERE school_id=? AND Session=? AND status=1";
        List<Object> args = new ArrayList<>(List.of(school, session));
        if (!WebsiteRepository.blank(type) && !"0".equals(type) && !"--select--".equalsIgnoreCase(type)) { sql += " AND type=?"; args.add(type); }
        return jdbc.queryForList(sql + " AND ISNULL(noticetitle_id,0)<>0 ORDER BY TRY_CONVERT(int,noticetitle_id) DESC", args.toArray());
    }

    @Transactional
    public Map<String, Object> saveNotice(String school, String staff, String session, String type, String category,
                                           String noticeDate, String description, String classIds, String studentIds,
                                           MultipartFile file) {
        writeGuard.requireWritable();
        if (WebsiteRepository.blank(session) || WebsiteRepository.blank(type)) throw new ResponseStatusException(BAD_REQUEST, "Session and notice type are required");
        if (WebsiteRepository.blank(description) && (file == null || file.isEmpty())) throw new ResponseStatusException(BAD_REQUEST, "Please mention description or upload a file");
        String path = saveFile(file, school, "exam_schadule", Set.of("jpg","jpeg","gif","png","pdf","doc","docx","txt"));
        String noticeId = String.valueOf(nextNumeric("school_notice", "noticetitle_id", school));
        List<Integer> classes = ints(classIds);
        List<Integer> students = ints(studentIds);
        String message = "Notice Uploaded For '" + type + ".' " + safe(description);
        if ("school".equalsIgnoreCase(type)) {
            if (classes.isEmpty()) throw new ResponseStatusException(BAD_REQUEST, "Please choose classes");
            for (Integer classId : classes) {
                jdbc.update("INSERT INTO school_notice(class_id,stu_id,title,Sec_id,type,file_path,category,Session,create_date,created_by,school_id,status,Description,notice_date,noticetitle_id) VALUES(?,0,'',?, ?,?,?,?,GETDATE(),?,?,1,?,?,?)", classId, "0", type, path, category, session, staff, school, description, noticeDate, noticeId);
                jdbc.update("INSERT INTO notification(user_id,notification_type,message,img_path,seen_status,school_id,session,user_type,created_by,status) SELECT CAST(stu_id AS varchar(50)),'notice',?,?, 'unseen',?,?, 'S',?,1 FROM TB_StudentMaster WHERE school_id=? AND session=? AND LOWER(status)='registered' AND class_id=?", message, path, school, session, staff, school, session, classId);
            }
        } else {
            if (students.isEmpty()) throw new ResponseStatusException(BAD_REQUEST, "Please choose students");
            for (Integer student : students) {
                jdbc.update("INSERT INTO school_notice(type,title,file_path,category,Session,create_date,created_by,school_id,status,Description,notice_date,stu_id,noticetitle_id,class_id,Sec_id) SELECT ?, '', ?, ?, ?, GETDATE(), ?, ?, 1, ?, ?, ?, ?, class_id, sec_id FROM TB_StudentMaster WHERE school_id=? AND session=? AND stu_id=?", type, path, category, session, staff, school, description, noticeDate, student, noticeId, school, session, student);
                jdbc.update("INSERT INTO notification(user_id,notification_type,message,img_path,seen_status,school_id,session,user_type,created_by,status) VALUES(CAST(? AS varchar(50)),'notice',?,?, 'unseen',?,?, 'S',?,1)", student, message, path, school, session, staff);
            }
        }
        return row("message", "Record saved successfully", "id", noticeId);
    }

    public void deleteNotice(String school, String id) { writeGuard.requireWritable(); jdbc.update("UPDATE school_notice SET status=0,modified_date=GETDATE() WHERE school_id=? AND noticetitle_id=?", school, id); }

    public List<Map<String, Object>> news(String school) {
        return jdbc.queryForList("SELECT n.title_id AS id,n.news_title AS title,n.news_description AS description,n.created_date AS createdDate,n.NewsDate AS newsDate,n.new_link AS link,ni.file_path AS filePath FROM upload_news n LEFT JOIN (SELECT title_id,MAX(file_path) file_path FROM news_image GROUP BY title_id) ni ON ni.title_id=n.title_id WHERE n.school_id=? AND n.status=1 ORDER BY TRY_CONVERT(date,n.NewsDate),n.id DESC", school);
    }

    @Transactional
    public Map<String, Object> saveNews(String school, String staff, String session, String title, String link, String description, String date, List<MultipartFile> files) {
        writeGuard.requireWritable();
        List<MultipartFile> valid = nonEmpty(files);
        if (valid.isEmpty() && !"shps_kg1".equalsIgnoreCase(school)) throw new ResponseStatusException(BAD_REQUEST, "Please upload a file");
        String id = uniqueNumericId("upload_news", "title_id", school);
        jdbc.update("INSERT INTO upload_news(news_title,news_description,created_date,created_by,school_id,session,title_id,status,NewsDate,new_link) VALUES(?,?,CONVERT(varchar(50),GETDATE(),120),?,?,?,1,?,?)", title, description, staff, school, session, id, date, link);
        if (valid.isEmpty()) jdbc.update("INSERT INTO news_image(title_id,file_path,session,school_id) VALUES(?,?,?,?)", id, "", session, school);
        for (MultipartFile file : valid) jdbc.update("INSERT INTO news_image(title_id,file_path,session,school_id) VALUES(?,?,?,?)", id, saveFile(file, school, "news", Set.of("jpg","jpeg","gif","png","pdf","doc","docx","txt")), session, school);
        return row("message", "record saved successfully", "id", id);
    }

    public void deleteNews(String school, String id) { writeGuard.requireWritable(); jdbc.update("UPDATE upload_news SET status=0 WHERE school_id=? AND title_id=?", school, id); }

    public List<Map<String, Object>> examSchedules(String school, String session) {
        return jdbc.queryForList("SELECT u.row_id AS id,u.class_id AS classId,cn.class_name AS className,u.exam_type_id AS examTypeId,COALESCE(e.exam_type,e.exam_type_dis,'') AS examType,u.title,u.upload_file AS filePath FROM tb_exam_schedule_uploads u LEFT JOIN class_name cn ON cn.id=TRY_CONVERT(int,u.class_id) AND cn.school_id=u.school_id LEFT JOIN exam_type e ON e.exam_type_id=TRY_CONVERT(int,u.exam_type_id) AND e.school_id=u.school_id WHERE u.school_id=? AND u.session=? AND u.status=1 ORDER BY u.row_id DESC", school, session);
    }

    @Transactional
    public Map<String, Object> saveExamSchedule(String school, String staff, String session, String classId, String examTypeId, String title, MultipartFile file) {
        writeGuard.requireWritable();
        if (WebsiteRepository.blank(classId) || WebsiteRepository.blank(examTypeId) || WebsiteRepository.blank(title) || file == null || file.isEmpty()) throw new ResponseStatusException(BAD_REQUEST, "Class, exam, title and file are required");
        String path = saveFile(file, school, "exam_schadule", Set.of("pdf","doc","docx","xls","xlsx","jpg","jpeg","png"));
        jdbc.update("INSERT INTO tb_exam_schedule_uploads(class_id,exam_type_id,title,upload_file,session,school_id,created_by,created_date,modified_by,modified_date,status) VALUES(?,?,?,?,?,?,?,GETDATE(),?,GETDATE(),1)", classId, examTypeId, title.toUpperCase(Locale.ROOT), path, session, school, staff, staff);
        return row("message", "record saved successfully");
    }

    public void deleteExamSchedule(String school, String id) { writeGuard.requireWritable(); jdbc.update("UPDATE tb_exam_schedule_uploads SET status=0 WHERE school_id=? AND row_id=?", school, id); }

    public List<Map<String, Object>> galleries(String school, String session) {
        List<Map<String, Object>> rows = jdbc.queryForList("SELECT title_id AS id,title,submission_date AS submissionDate,descriptions AS description,Drive_Link AS driveLink FROM photoGallery WHERE school_id=? AND status=1 AND (?='' OR session=?) ORDER BY TRY_CONVERT(date,submission_date) DESC,id DESC", school, session, session);
        for (Map<String, Object> row : rows) row.put("images", jdbc.queryForList("SELECT id,file_path AS filePath FROM photoGallery_images WHERE school_id=? AND title_id=? AND session=? AND status=1 ORDER BY id", school, row.get("id"), session));
        return rows;
    }

    @Transactional
    public Map<String, Object> saveGallery(String school, String staff, String session, String title, String date, String description, String driveLink, List<MultipartFile> files) {
        writeGuard.requireWritable();
        List<MultipartFile> valid = nonEmpty(files);
        if (valid.isEmpty() || valid.size() > 20) throw new ResponseStatusException(BAD_REQUEST, valid.isEmpty() ? "Please select files" : "You can upload maximum 20 images at a time.");
        Integer duplicate = jdbc.queryForObject("SELECT COUNT(1) FROM photoGallery WHERE school_id=? AND session=? AND title=? AND status=1 AND submission_date=?", Integer.class, school, session, title, date);
        if (duplicate != null && duplicate > 0) throw new ResponseStatusException(BAD_REQUEST, "Already exists");
        String id = String.valueOf(nextNumeric("photoGallery", "title_id", school));
        jdbc.update("INSERT INTO photoGallery(title_id,title,submission_date,status,session,school_id,created_by,created_date,modified_by,modified_date,descriptions,Drive_Link) VALUES(?,?,?,1,?,?,?,GETDATE(),'','',?,?)", id, title, date, session, school, staff, description, driveLink);
        for (MultipartFile file : valid) jdbc.update("INSERT INTO photoGallery_images(title_id,file_path,status,session,school_id,created_by,created_date) VALUES(?,?,1,?,?,?,GETDATE())", id, saveFile(file, school, "photoGallery", Set.of("jpg","jpeg","jfif","png","gif","txt")), session, school, staff);
        return row("message", "Photos Uploaded Successfully.", "id", id);
    }

    @Transactional
    public Map<String, Object> updateGallery(String school, String staff, String session, String id, String title, String date, List<MultipartFile> files) {
        writeGuard.requireWritable();
        jdbc.update("UPDATE photoGallery SET title=?,submission_date=?,session=? WHERE school_id=? AND title_id=? AND session=? AND status=1", title, date, session, school, id, session);
        for (MultipartFile file : nonEmpty(files)) jdbc.update("INSERT INTO photoGallery_images(title_id,file_path,status,session,school_id,created_by,created_date) VALUES(?,?,1,?,?,?,GETDATE())", id, saveFile(file, school, "photoGallery", Set.of("jpg","jpeg","jfif","png","gif","txt")), session, school, staff);
        return row("message", "Gallery updated");
    }

    @Transactional
    public void deleteGallery(String school, String session, String id) { writeGuard.requireWritable(); jdbc.update("UPDATE photoGallery SET status=0,modified_date=GETDATE() WHERE school_id=? AND title_id=? AND session=? AND status=1", school, id, session); jdbc.update("UPDATE photoGallery_images SET status=0 WHERE school_id=? AND title_id=? AND session=? AND status=1", school, id, session); }

    public List<Map<String, Object>> videos(String school, String session) {
        return jdbc.queryForList("SELECT v.title_id AS id,v.title,v.submission_date AS submissionDate,v.descriptions AS description,COALESCE(x.file_path,'') AS videoUrl FROM VideoGallery v LEFT JOIN (SELECT title_id,MAX(file_path) file_path FROM VideoGallery_video WHERE school_id=? AND status=1 GROUP BY title_id) x ON x.title_id=v.title_id WHERE v.school_id=? AND v.status=1 AND (?='' OR v.session=?) ORDER BY TRY_CONVERT(date,v.submission_date) DESC,v.id DESC", school, school, session, session);
    }

    @Transactional
    public Map<String, Object> saveVideo(String school, String staff, String session, String title, String date, String description, String videoUrl) {
        writeGuard.requireWritable();
        if (WebsiteRepository.blank(title) || WebsiteRepository.blank(date) || WebsiteRepository.blank(videoUrl)) throw new ResponseStatusException(BAD_REQUEST, "Title, date and video URL are required");
        Integer duplicate = jdbc.queryForObject("SELECT COUNT(1) FROM VideoGallery WHERE school_id=? AND session=? AND title=? AND status=1 AND submission_date=?", Integer.class, school, session, title, date);
        if (duplicate != null && duplicate > 0) throw new ResponseStatusException(BAD_REQUEST, "Already exists");
        String id = String.valueOf(nextNumeric("photoGallery", "title_id", school));
        jdbc.update("INSERT INTO VideoGallery(title_id,title,submission_date,status,session,school_id,created_by,created_date,modified_by,modified_date,descriptions) VALUES(?,?,?,1,?,?,?,GETDATE(),'','',?)", id, title, date, session, school, staff, description);
        jdbc.update("INSERT INTO VideoGallery_video(title_id,file_path,status,session,school_id,created_by,created_date) VALUES(?,?,1,?,?,?,GETDATE())", id, videoUrl.trim(), session, school, staff);
        return row("message", "Record Save ..!!", "id", id);
    }

    public void deleteVideo(String school, String id) { writeGuard.requireWritable(); jdbc.update("UPDATE VideoGallery SET status=0 WHERE school_id=? AND title_id=?", school, id); jdbc.update("UPDATE VideoGallery_video SET status=0 WHERE school_id=? AND title_id=?", school, id); }

    public List<Map<String, Object>> events(String school, String session) {
        List<Map<String, Object>> rows = jdbc.queryForList("SELECT event_id AS id,event_name AS name,event_date AS eventDate,descriptions AS description,MAX(file_name) AS filePath FROM latest_events WHERE school_id=? AND session=? GROUP BY event_id,event_name,event_date,descriptions ORDER BY TRY_CONVERT(date,event_date) DESC,TRY_CONVERT(int,event_id) DESC", school, session);
        for (Map<String, Object> row : rows) row.put("images", jdbc.queryForList("SELECT file_name AS filePath FROM latest_events WHERE school_id=? AND session=? AND event_id=? ORDER BY id", school, session, row.get("id")));
        return rows;
    }

    @Transactional
    public Map<String, Object> saveEvent(String school, String staff, String session, String name, String date, String description, List<MultipartFile> files) {
        writeGuard.requireWritable();
        List<MultipartFile> valid = nonEmpty(files);
        if (WebsiteRepository.blank(name) || WebsiteRepository.blank(date) || valid.isEmpty()) throw new ResponseStatusException(BAD_REQUEST, "Event name, date and files are required");
        String id = String.valueOf(nextNumeric("latest_events", "event_id", school));
        for (MultipartFile file : valid) jdbc.update("INSERT INTO latest_events(event_id,event_name,session,file_name,school_id,created_by,modified_by,modified_date,event_date,descriptions,descriptions_html) VALUES(?,?,?,?,?,?, '','',?,?,?)", id, name, session, saveFile(file, school, "events", Set.of("jpg","jpeg","png")), school, staff, date, stripHtml(description), description);
        return row("message", "Data Uploaded Successfully", "id", id);
    }

    public void deleteEvent(String school, String id) { writeGuard.requireWritable(); jdbc.update("DELETE FROM latest_events WHERE school_id=? AND event_id=?", school, id); }

    public List<Map<String, Object>> transferCertificates(String school, String session) {
        return jdbc.queryForList("SELECT tc.stu_id AS studentId,tc.tc_no AS tcNo,tbs.registration_no AS registrationNo,tc.file_path AS filePath,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS name FROM transfer_certificate tc JOIN TB_StudentMaster tbs ON tbs.stu_id=tc.stu_id AND tbs.session=tc.session AND tbs.school_id=tc.school_id LEFT JOIN student_other_info so ON so.school_id=tc.school_id AND so.registration_no=tbs.registration_no WHERE tc.school_id=? AND tc.session=? AND LOWER(tbs.status)='withdrawn' AND ISNULL(tc.file_path,'')<>'' ORDER BY tc.registration_no", school, session);
    }

    public Map<String, Object> withdrawnStudent(String school, String session, String admissionNo) {
        List<Map<String, Object>> rows = jdbc.queryForList("SELECT TOP 1 stu_id AS studentId,registration_no AS registrationNo,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS name FROM TB_StudentMaster st LEFT JOIN student_other_info so ON so.school_id=st.school_id AND so.registration_no=st.registration_no WHERE st.school_id=? AND st.session=? AND LOWER(st.status)='withdrawn' AND st.registration_no=?", school, session, admissionNo);
        if (rows.isEmpty()) throw new ResponseStatusException(BAD_REQUEST, "Admission No not Exist");
        return rows.get(0);
    }

    @Transactional
    public Map<String, Object> saveTransferCertificate(String school, String staff, String session, String admissionNo, MultipartFile file) {
        writeGuard.requireWritable();
        if (file == null || file.isEmpty()) throw new ResponseStatusException(BAD_REQUEST, "Please Upload File");
        String ext = extension(file.getOriginalFilename());
        if (!"pdf".equalsIgnoreCase(ext)) throw new ResponseStatusException(BAD_REQUEST, "Sorry ! You can not Upload File");
        Map<String, Object> student = withdrawnStudent(school, session, admissionNo);
        String path = saveFile(file, school, "transfer_certificate", Set.of("pdf"));
        Integer exists = jdbc.queryForObject("SELECT COUNT(1) FROM transfer_certificate WHERE school_id=? AND stu_id=? AND session=?", Integer.class, school, student.get("studentId"), session);
        if (exists != null && exists > 0) jdbc.update("UPDATE transfer_certificate SET file_path=?,modified_date=GETDATE(),modified_by=? WHERE school_id=? AND stu_id=? AND session=?", path, staff, school, student.get("studentId"), session);
        else jdbc.update("INSERT INTO transfer_certificate(tc_no,session,school_id,created_date,created_by,modified_date,modified_by,stu_id,registration_no,file_path) VALUES('',?,?,GETDATE(),?,GETDATE(),?,?,?,?)", session, school, staff, staff, student.get("studentId"), admissionNo, path);
        return row("message", "TC upload Successfully", "filePath", path);
    }

    public Resource file(String relative) {
        String clean = relative == null ? "" : relative.replace('\\', '/').replaceFirst("^/+", "").replaceFirst("^~/", "");
        Path legacyRoot = Paths.get(System.getProperty("user.dir")).getParent().getParent().resolve("Old").normalize();
        Path path = storageRoot.resolve(clean).normalize();
        if ((!Files.exists(path) || !Files.isRegularFile(path))) {
            Path legacy = legacyRoot.resolve(clean).normalize();
            if (legacy.startsWith(legacyRoot) && Files.exists(legacy) && Files.isRegularFile(legacy)) path = legacy;
        }
        if ((!path.startsWith(storageRoot) && !path.startsWith(legacyRoot)) || !Files.exists(path) || !Files.isRegularFile(path)) throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "File not found");
        return new FileSystemResource(path);
    }

    private String saveFile(MultipartFile file, String school, String folder, Set<String> allowed) {
        if (file == null || file.isEmpty()) return "";
        String ext = extension(file.getOriginalFilename());
        if (!allowed.contains(ext.toLowerCase(Locale.ROOT))) throw new ResponseStatusException(BAD_REQUEST, "Unsupported file type");
        String original = file.getOriginalFilename() == null ? "file" : Paths.get(file.getOriginalFilename()).getFileName().toString();
        String base = original.replaceFirst("\\.[^.]+$", "").replaceAll("[^A-Za-z0-9._-]", "_");
        String name = base + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10) + "_" + school + (ext.isBlank() ? "" : "." + ext);
        Path dir = storageRoot.resolve("Uploads_doc").resolve(folder).normalize();
        try { Files.createDirectories(dir); Files.copy(file.getInputStream(), dir.resolve(name), StandardCopyOption.REPLACE_EXISTING); }
        catch (IOException e) { throw new ResponseStatusException(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, "Could not save upload", e); }
        return "Uploads_doc/" + folder + "/" + name;
    }

    private int nextNumeric(String table, String column, String school) {
        Integer value = jdbc.queryForObject("SELECT ISNULL(MAX(TRY_CONVERT(int," + column + ")),0)+1 FROM " + table + " WHERE school_id=?", Integer.class, school);
        return value == null ? 1 : value;
    }

    private String uniqueNumericId(String table, String column, String school) {
        for (int n = 0; n < 10; n++) { String id = String.valueOf(ThreadLocalRandom.current().nextInt(10000, 99999)); Integer count = jdbc.queryForObject("SELECT COUNT(1) FROM " + table + " WHERE school_id=? AND " + column + "=?", Integer.class, school, id); if (count == null || count == 0) return id; }
        return String.valueOf(System.currentTimeMillis()).substring(3);
    }

    private static List<MultipartFile> nonEmpty(List<MultipartFile> files) { return files == null ? List.of() : files.stream().filter(f -> f != null && !f.isEmpty()).toList(); }
    private static List<Integer> ints(String raw) { if (WebsiteRepository.blank(raw)) return new ArrayList<>(); List<Integer> out = new ArrayList<>(); for (String x : raw.split(",")) try { int n = Integer.parseInt(x.trim()); if (n > 0) out.add(n); } catch (Exception ignored) {} return out; }
    private static String extension(String filename) { if (filename == null || !filename.contains(".")) return ""; return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT); }
    private static String safe(String value) { return value == null ? "" : value.replace("'", ""); }
    private static String stripHtml(String value) { return value == null ? "" : value.replaceAll("<[^>]*>", ""); }
    private static Map<String, Object> row(Object... values) { Map<String, Object> out = new LinkedHashMap<>(); for (int i = 0; i + 1 < values.length; i += 2) out.put(String.valueOf(values[i]), values[i + 1]); return out; }
}
