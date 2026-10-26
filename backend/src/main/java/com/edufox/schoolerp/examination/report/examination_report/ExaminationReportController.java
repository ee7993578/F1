package com.edufox.schoolerp.examination.report.examination_report;
import com.edufox.schoolerp.examination.setup.exam_setup.*;
import com.edufox.schoolerp.examination.update.delete_marks.*;
import com.edufox.schoolerp.examination.work.examination_work.*;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/examination/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class ExaminationReportController {
    private final ExaminationReportService service;

    @GetMapping("/bootstrap")
    public Map<String,Object> bootstrap(Authentication a){Claims c=claims(a);return service.bootstrap(school(c),String.valueOf(c.get("staffUid")));}
    @GetMapping("/options")
    public Map<String,Object> options(@RequestParam String session,@RequestParam(required=false,defaultValue="0") int sectionId,@RequestParam(required=false,defaultValue="0") int classId,@RequestParam(required=false,defaultValue="") String termId,Authentication a){return service.options(school(claims(a)),session,sectionId,classId,termId);}
    @PostMapping("/data")
    public Map<String,Object> data(@RequestBody Map<String,Object> body,Authentication a){return service.data(school(claims(a)),body);}
    @PostMapping(value="/annual-report",consumes="multipart/form-data")
    public Map<String,Object> saveAnnual(@RequestParam String title,@RequestParam(required=false,defaultValue="") String date,@RequestPart MultipartFile file,Authentication a)throws IOException{Claims c=claims(a);return service.saveAnnual(school(c),String.valueOf(c.get("staffUid")),title,date,file);}
    @DeleteMapping("/annual-report/{id}")
    public Map<String,Object> deleteAnnual(@PathVariable String id,Authentication a){return service.deleteAnnual(school(claims(a)),id);}
    private static Claims claims(Authentication a){if(a==null||!(a.getDetails() instanceof Claims c))throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Authenticated claims are missing");return c;}
    private static String school(Claims c){String s=c.get("schoolId",String.class);if(s==null||s.isBlank())throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"School claim is missing");return s;}
}
