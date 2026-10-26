package com.edufox.schoolerp.student.setup.alumni;
import com.edufox.schoolerp.student.setup.class_representative.*;
import com.edufox.schoolerp.student.setup.exam_configuration.*;
import com.edufox.schoolerp.student.setup.house_master.*;
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
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;
import javax.imageio.ImageIO;

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
            ORDER BY created_date DESC, ID DESC
            """, schoolId);
    }
    @Transactional
    public AlumniDtos.AlumniResponse save(String schoolId, AlumniDtos.AlumniRequest r) {
        if (text(r.fullName()).isBlank() || text(r.studentId()).isBlank() || text(r.batch()).isBlank())
            throw new IllegalArgumentException("Name, student ID and batch are required");
        String photoPath = savePhoto(r);
        jdbc.update("""
            INSERT INTO Alumni
              (FullName,PhoneNumber,EmailAddress,Qualification,CurrentStatus,DOB,StudentId,Batch,
               AlumniPhoto,Address,Gender,school_id,status)
            VALUES (?,?,?,?,?,TRY_CONVERT(date,?,23),?,?,?,?,?,?,'Active')
            """, text(r.fullName()), text(r.phoneNumber()), text(r.emailAddress()), text(r.qualification()),
            text(r.currentStatus()), text(r.dob()), text(r.studentId()), text(r.batch()),
            photoPath, text(r.address()), text(r.gender()), schoolId);
        Integer id=jdbc.queryForObject("SELECT TOP 1 ID FROM Alumni WHERE school_id=? ORDER BY ID DESC",Integer.class,schoolId);
        return new AlumniDtos.AlumniResponse(id,"Alumni registered");
    }
    private String savePhoto(AlumniDtos.AlumniRequest r) {
        String data=text(r.alumniPhotoBase64());
        if (data.isBlank()) return text(r.alumniPhoto());
        try {
            String encoded=data.contains(",") ? data.substring(data.indexOf(',')+1) : data;
            byte[] bytes=Base64.getDecoder().decode(encoded);
            BufferedImage source=ImageIO.read(new ByteArrayInputStream(bytes));
            if (source==null) throw new IllegalArgumentException("Invalid image format !! Kindly upload Image of correct format");
            BufferedImage resized=new BufferedImage(150,150,BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics=resized.createGraphics();
            graphics.drawImage(source.getScaledInstance(150,150,Image.SCALE_SMOOTH),0,0,null);
            graphics.dispose();
            String original=text(r.alumniPhotoName()).toLowerCase();
            String extension=original.endsWith(".png")?".PNG":original.endsWith(".gif")?".GIF":".JPG";
            String name=UUID.randomUUID().toString().replace("-","")+extension;
            Path directory=Paths.get(System.getProperty("user.dir"),"Uploads_doc","alumni_images");
            Files.createDirectories(directory);
            ImageIO.write(resized,"jpg",directory.resolve(name).toFile());
            return "~/Uploads_doc/alumni_images/"+name;
        } catch (IllegalArgumentException ex) { throw ex; }
        catch (Exception ex) { throw new IllegalArgumentException("Unable to save alumni photo",ex); }
    }
    private static String text(String v){return v==null?"":v.trim();}
}
