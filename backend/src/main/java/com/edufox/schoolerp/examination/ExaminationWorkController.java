package com.edufox.schoolerp.examination;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/examination/work")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class ExaminationWorkController {
    private final ExaminationWorkService service;
    @GetMapping("/bootstrap") public Map<String,Object> bootstrap(Authentication a){Claims c=claims(a);return service.bootstrap(school(c),String.valueOf(c.get("staffUid")));}
    @GetMapping("/terms") public List<Map<String,Object>> terms(@RequestParam String session,@RequestParam(required=false,defaultValue="0") Integer classId,Authentication a){return service.terms(school(claims(a)),session,classId);}
    @GetMapping("/subjects") public List<Map<String,Object>> subjects(@RequestParam String session,@RequestParam int sectionId,@RequestParam(required=false,defaultValue="") String facultyId,Authentication a){return service.subjects(school(claims(a)),session,sectionId,facultyId);}
    @GetMapping("/exams") public List<Map<String,Object>> exams(@RequestParam String session,@RequestParam int sectionId,@RequestParam String termId,Authentication a){return service.exams(school(claims(a)),session,sectionId,termId);}
    @GetMapping("/marks/options") public Map<String,Object> markOptions(@RequestParam String session,@RequestParam int sectionId,@RequestParam String termId,@RequestParam String examTypeId,@RequestParam String subjectId,Authentication a){return service.markOptions(school(claims(a)),session,sectionId,termId,examTypeId,subjectId);}
    @GetMapping("/learner-values") public List<Map<String,Object>> learnerValues(@RequestParam String session,@RequestParam String attributeId,Authentication a){return service.learnerValues(school(claims(a)),session,attributeId);}
    @GetMapping("/reportcard-subcategories") public List<Map<String,Object>> reportcardSubcategories(@RequestParam String session,@RequestParam String categoryId,Authentication a){return service.reportcardSubcategories(school(claims(a)),session,categoryId);}
    @GetMapping("/reportcard-values") public List<Map<String,Object>> reportcardValues(@RequestParam String session,@RequestParam String subcategoryId,Authentication a){return service.reportcardValues(school(claims(a)),session,subcategoryId);}
    @GetMapping("/marks") public List<Map<String,Object>> marks(@RequestParam String session,@RequestParam int sectionId,@RequestParam String termId,@RequestParam String examTypeId,@RequestParam String subjectId,Authentication a){return service.marks(school(claims(a)),session,sectionId,termId,examTypeId,subjectId);}
    @PostMapping("/marks") public Map<String,Object> marks(@RequestBody Map<String,Object>b,Authentication a){Claims c=claims(a);service.saveMarks(school(c),String.valueOf(c.get("staffUid")),b);return Map.of("message","Record Saved Successfully");}
    @GetMapping("/preprimary/bootstrap") public Map<String,Object> preprimaryBootstrap(@RequestParam String session,@RequestParam int sectionId,@RequestParam(required=false,defaultValue="") String mode,Authentication a){Claims c=claims(a);return service.preprimaryBootstrap(school(c),session,sectionId,String.valueOf(c.get("staffUid")),mode);}
    @GetMapping("/preprimary/headers") public List<Map<String,Object>> preprimaryHeaders(@RequestParam String session,@RequestParam int sectionId,Authentication a){return service.preprimaryHeaders(school(claims(a)),session,sectionId);}
    @GetMapping("/preprimary/categories") public List<Map<String,Object>> preprimaryCategories(@RequestParam String session,@RequestParam int sectionId,@RequestParam String headerId,Authentication a){return service.preprimaryCategories(school(claims(a)),session,sectionId,headerId);}
    @GetMapping("/preprimary/rows") public List<Map<String,Object>> preprimaryRows(@RequestParam String session,@RequestParam int sectionId,@RequestParam(required=false,defaultValue="") String headerId,@RequestParam(required=false,defaultValue="") String categoryId,@RequestParam String termId,@RequestParam String examTypeId,@RequestParam(required=false,defaultValue="") String studentId,Authentication a){return service.preprimaryRows(school(claims(a)),session,sectionId,headerId,categoryId,termId,examTypeId,studentId);}
    @GetMapping("/preprimary/student-rows") public List<Map<String,Object>> preprimaryStudentRows(@RequestParam String session,@RequestParam int sectionId,@RequestParam String studentId,@RequestParam String termId,@RequestParam String examTypeId,Authentication a){return service.preprimaryStudentRows(school(claims(a)),session,sectionId,studentId,termId,examTypeId);}
    @PostMapping("/preprimary") public Map<String,Object> preprimary(@RequestBody Map<String,Object>b,Authentication a){Claims c=claims(a);service.savePreprimary(school(c),String.valueOf(c.get("staffUid")),b);return Map.of("message","Record Saved Successfully");}
    @GetMapping("/health") public List<Map<String,Object>> health(@RequestParam String session,@RequestParam int sectionId,@RequestParam String termId,Authentication a){return service.health(school(claims(a)),session,sectionId,termId);}
    @PostMapping("/health") public Map<String,Object> health(@RequestBody Map<String,Object>b,Authentication a){Claims c=claims(a);service.saveHealth(school(c),String.valueOf(c.get("staffUid")),b);return Map.of("message","Submitted Successfully!!");}
    @GetMapping("/indicator-grades") public List<Map<String,Object>> indicatorGrades(@RequestParam String session,@RequestParam int sectionId,@RequestParam String termId,@RequestParam(required=false,defaultValue="") String indicatorId,@RequestParam(required=false,defaultValue="") String categoryId,Authentication a){return service.indicatorGrades(school(claims(a)),session,sectionId,termId,indicatorId,categoryId);}
    @PostMapping("/indicator-grades") public Map<String,Object> indicatorGrades(@RequestBody Map<String,Object>b,Authentication a){Claims c=claims(a);service.saveIndicatorGrades(school(c),String.valueOf(c.get("staffUid")),b);return Map.of("message","Save successfully");}
    @GetMapping("/learner-profile") public List<Map<String,Object>> learnerProfile(@RequestParam String session,@RequestParam int sectionId,@RequestParam String termId,@RequestParam String attributeId,Authentication a){return service.learnerProfile(school(claims(a)),session,sectionId,termId,attributeId);}
    @PostMapping("/learner-profile") public Map<String,Object> learnerProfile(@RequestBody Map<String,Object>b,Authentication a){Claims c=claims(a);service.saveLearnerProfile(school(c),String.valueOf(c.get("staffUid")),b);return Map.of("message","Save successfully");}
    @GetMapping("/remarks") public List<Map<String,Object>> remarks(@RequestParam String session,@RequestParam int sectionId,@RequestParam String termId,@RequestParam String remarkType,@RequestParam(required=false,defaultValue="") String examTypeId,Authentication a){return service.remarks(school(claims(a)),session,sectionId,termId,remarkType,examTypeId);}
    @PostMapping("/remarks") public Map<String,Object> remarks(@RequestBody Map<String,Object>b,Authentication a){Claims c=claims(a);service.saveRemarks(school(c),String.valueOf(c.get("staffUid")),b);return Map.of("message","Record Saved");}
    @GetMapping("/reportcard-remarks") public List<Map<String,Object>> reportcardRemarks(@RequestParam String session,@RequestParam int sectionId,@RequestParam String termId,@RequestParam String categoryId,@RequestParam String subcategoryId,Authentication a){return service.reportcardRemarks(school(claims(a)),session,sectionId,termId,categoryId,subcategoryId);}
    @PostMapping("/reportcard-remarks") public Map<String,Object> reportcardRemarks(@RequestBody Map<String,Object>b,Authentication a){Claims c=claims(a);service.saveReportcardRemarks(school(c),String.valueOf(c.get("staffUid")),b);return Map.of("message","Record Saved");}
    @GetMapping("/green-sheet") public List<Map<String,Object>> greenSheet(@RequestParam String session,@RequestParam int sectionId,Authentication a){return service.greenSheet(school(claims(a)),session,sectionId);}
    @GetMapping("/upload/results") public List<Map<String,Object>> uploadResults(@RequestParam String session,@RequestParam int sectionId,Authentication a){return service.uploadResults(school(claims(a)),session,sectionId);}
    @PostMapping("/upload") public Map<String,Object> upload(@RequestParam String session,@RequestParam int sectionId,@RequestPart MultipartFile file,Authentication a)throws Exception{if(file.isEmpty())throw new IllegalArgumentException("Select an Excel file");Claims c=claims(a);int n=service.uploadMarks(school(c),String.valueOf(c.get("staffUid")),session,sectionId,file);return Map.of("message","Data uploaded successfully","count",n);}
    private static Claims claims(Authentication a){if(a==null||!(a.getDetails() instanceof Claims c))throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Authenticated claims are missing");return c;}
    private static String school(Claims c){String s=c.get("schoolId",String.class);if(s==null||s.isBlank())throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"School claim is missing");return s;}
}
