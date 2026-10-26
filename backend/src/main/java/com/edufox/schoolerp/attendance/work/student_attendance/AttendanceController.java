package com.edufox.schoolerp.attendance.work.student_attendance;
import com.edufox.schoolerp.attendance.report.attendance.*;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class AttendanceController {
    private final AttendanceService service;

    @GetMapping("/bootstrap") public Map<String,Object> bootstrap(Authentication a){Claims c=claims(a);return service.bootstrap(school(c),c.get("staffUid",String.class));}
    @GetMapping("/day") public List<Map<String,Object>> day(@RequestParam String session,@RequestParam int sectionId,@RequestParam String date,Authentication a){return service.dayRows(school(claims(a)),session,sectionId,date);}
    @PostMapping("/day") public Map<String,Object> saveDay(@RequestBody Map<String,Object> b,Authentication a){Claims c=claims(a);service.saveDay(school(c),c.get("staffUid",String.class),str(b,"session"),integer(b,"sectionId"),str(b,"date"),list(b,"records"));return Map.of("message","Record Updated Successfully");}
    @GetMapping("/month/options") public Map<String,Object> monthOptions(@RequestParam String session,@RequestParam int sectionId,@RequestParam(required=false) Integer term,Authentication a){return service.monthOptions(school(claims(a)),session,sectionId,term);}
    @GetMapping("/month") public Map<String,Object> month(@RequestParam String session,@RequestParam int sectionId,@RequestParam int term,@RequestParam int month,@RequestParam int year,Authentication a){return service.monthlyRows(school(claims(a)),session,sectionId,term,month,year);}
    @PostMapping("/month") public Map<String,Object> saveMonth(@RequestBody Map<String,Object> b,Authentication a){Claims c=claims(a);service.saveMonthly(school(c),c.get("staffUid",String.class),str(b,"session"),integer(b,"sectionId"),integer(b,"term"),integer(b,"month"),integer(b,"year"),list(b,"records"));return Map.of("message","Record Updated Successfully");}
    @GetMapping("/delete") public List<Map<String,Object>> deleteLookup(@RequestParam String session,@RequestParam int sectionId,@RequestParam(required=false,defaultValue="") String admissionNo,@RequestParam String from,@RequestParam String to,Authentication a){return service.deleteRows(school(claims(a)),session,sectionId,admissionNo,from,to);}
    @PostMapping("/delete") public Map<String,Object> delete(@RequestBody Map<String,Object> b,Authentication a){Claims c=claims(a);int n=service.deleteRows(school(c),str(b,"session"),integer(b,"sectionId"),str(b,"admissionNo"),str(b,"from"),str(b,"to"),c.get("staffUid",String.class));return Map.of("deleted",n,"message","Record Deleted Successfully");}
    @PostMapping("/holiday") public Map<String,Object> holiday(@RequestBody Map<String,Object> b,Authentication a){Claims c=claims(a);return Map.of("inserted",service.saveHoliday(school(c),str(b,"session"),intList(b.get("classIds")),str(b,"date"),c.get("staffUid",String.class)),"message","Data Save Successfully");}
    @GetMapping("/rfid/campus-count") public List<Map<String,Object>> campusCount(@RequestParam String session,@RequestParam String date,@RequestParam(defaultValue="0") int sectionId,Authentication a){return service.rfidCampusCount(school(claims(a)),session,date,sectionId);}
    @GetMapping("/rfid/campus-student") public List<Map<String,Object>> campusStudent(@RequestParam String session,@RequestParam String date,@RequestParam(defaultValue="0") int sectionId,Authentication a){return service.rfidCampusStudent(school(claims(a)),session,date,sectionId);}
    @GetMapping("/rfid/transport-count") public List<Map<String,Object>> transportCount(@RequestParam String session,@RequestParam String date,Authentication a){return service.rfidTransportCount(school(claims(a)),session,date);}
    @GetMapping("/rfid/transport-student") public List<Map<String,Object>> transportStudent(@RequestParam String session,@RequestParam String date,@RequestParam(defaultValue="0") int routeId,Authentication a){return service.rfidTransportStudent(school(claims(a)),session,date,routeId);}
    @GetMapping("/rfid/matrix") public Map<String,Object> rfidMatrix(@RequestParam String session,@RequestParam(defaultValue="0") int sectionId,@RequestParam(defaultValue="0") int year,@RequestParam(defaultValue="0") int month,@RequestParam(required=false,defaultValue="") String date,Authentication a){return service.rfidMatrix(school(claims(a)),session,sectionId,year,month,date);}
    @GetMapping("/reports/bootstrap") public Map<String,Object> reportBootstrap(@RequestParam(required=false,defaultValue="") String session,Authentication a){Claims c=claims(a);return service.reportBootstrap(school(c),c.get("staffUid",String.class),session);}
    @GetMapping("/reports/percentage") public Map<String,Object> percentageReport(@RequestParam String session,
            @RequestParam(defaultValue="") String classIds,
            @RequestParam(defaultValue="") String sectionIds,
            @RequestParam(defaultValue="") String fromPer,
            @RequestParam(defaultValue="") String toPer,
            @RequestParam(defaultValue="") String fromDate,
            @RequestParam(defaultValue="") String toDate,
            Authentication a){
        Claims c=claims(a);
        return service.percentageReport(school(c),c.get("staffUid",String.class),session,
            intList(classIds),intList(sectionIds),fromPer,toPer,fromDate,toDate);
    }
    @GetMapping("/reports/status") public Map<String,Object> statusReport(@RequestParam String session,@RequestParam(defaultValue="0") int sectionId,@RequestParam(required=false,defaultValue="") String date,Authentication a){Claims c=claims(a);return service.statusReport(school(c),c.get("staffUid",String.class),session,sectionId,date);}
    @GetMapping("/reports/total") public Map<String,Object> totalReport(@RequestParam String session,@RequestParam int sectionId,Authentication a){Claims c=claims(a);return service.totalReport(school(c),c.get("staffUid",String.class),session,sectionId);}

    private static Claims claims(Authentication a){if(a==null||!(a.getDetails() instanceof Claims c))throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Authenticated claims are missing");return c;}
    private static String school(Claims c){String s=c.get("schoolId",String.class);if(s==null||s.isBlank())throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"School claim is missing");return s;}
    private static String str(Map<String,Object>b,String k){Object v=b.get(k);return v==null?"":String.valueOf(v);}
    private static int integer(Map<String,Object>b,String k){try{return Integer.parseInt(str(b,k));}catch(Exception e){return 0;}}
    @SuppressWarnings("unchecked") private static List<Map<String,Object>> list(Map<String,Object>b,String k){Object v=b.get(k);return v instanceof List<?> l?(List<Map<String,Object>>)(List<?>)l:List.of();}
    private static List<Integer> intList(Object v){if(!(v instanceof List<?> l))return List.of();return l.stream().map(x->{try{return Integer.valueOf(String.valueOf(x));}catch(Exception e){return 0;}}).filter(x->x>0).toList();}
    private static List<Integer> intList(String value){
        if(value==null||value.isBlank()) return List.of();
        return java.util.Arrays.stream(value.split(","))
            .map(String::trim).filter(x->!x.isBlank()).map(x->{try{return Integer.valueOf(x);}catch(Exception e){return 0;}})
            .filter(x->x>0).toList();
    }
}
