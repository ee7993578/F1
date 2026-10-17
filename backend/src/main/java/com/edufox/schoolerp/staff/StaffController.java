package com.edufox.schoolerp.staff;

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
@RequestMapping("/api/staff")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class StaffController {
    private final StaffService service;

    @GetMapping("/bootstrap") public Map<String,Object> bootstrap(Authentication a,@RequestParam(required=false) String session){return service.bootstrap(school(a),session);}
    @GetMapping public List<Map<String,Object>> list(Authentication a,@RequestParam(required=false) String session,@RequestParam(required=false) String search,@RequestParam(defaultValue="false") boolean withdrawn){return service.list(school(a),session,search,withdrawn);}
    @GetMapping("/{id}") public Map<String,Object> find(Authentication a,@PathVariable String id){return service.find(school(a),id);}
    @PostMapping public StaffDtos.MutationResponse register(Authentication a,@RequestParam(required=false) String session,@RequestBody Map<String,Object>b){return service.register(school(a),staff(a),session,b);}
    @PutMapping("/{id}") public StaffDtos.MutationResponse update(Authentication a,@PathVariable String id,@RequestBody Map<String,Object>b){return service.update(school(a),id,b,staff(a));}
    @DeleteMapping("/{id}") public StaffDtos.MutationResponse withdraw(Authentication a,@PathVariable String id,@RequestParam String session,@RequestBody(required=false) Map<String,Object>b){return service.withdraw(school(a),id,session,b==null?"":String.valueOf(b.getOrDefault("reason","")),staff(a));}
    @PostMapping("/{id}/reregister") public StaffDtos.MutationResponse reregister(Authentication a,@PathVariable String id,@RequestParam String session){return service.reregister(school(a),id,session,staff(a));}
    @PutMapping("/{id}/credentials") public StaffDtos.MutationResponse credentials(Authentication a,@PathVariable String id,@RequestBody Map<String,Object>b){return service.credentials(school(a),id,b);}
    @GetMapping("/reports/class-teachers") public List<Map<String,Object>> classTeachers(Authentication a,@RequestParam String session){return service.classTeachers(school(a),session);}
    @GetMapping("/class-teacher-setup") public Map<String,Object> classTeacherSetup(Authentication a,@RequestParam String session){return service.classTeacherSetup(school(a),session);}
    @PutMapping("/class-teachers") public StaffDtos.MutationResponse saveClassTeachers(Authentication a,@RequestBody StaffDtos.ClassTeacherRequest r){return service.saveClassTeachers(school(a),staff(a),r);}
    @GetMapping("/job-config") public List<Map<String,Object>> jobConfig(Authentication a,@RequestParam(required=false) String session){return service.jobConfig(school(a),session);}
    @GetMapping("/job-config-options") public Map<String,Object> jobConfigOptions(Authentication a){return service.jobConfigOptions(school(a));}
    @GetMapping("/job-options") public Map<String,Object> jobApplicationOptions(Authentication a,@RequestParam(required=false) String appliedPost){return service.jobApplicationOptions(school(a),appliedPost);}
    @GetMapping("/job-applications") public List<Map<String,Object>> jobApplications(Authentication a,@RequestParam(required=false) String appliedPost,@RequestParam(required=false) String subject,@RequestParam(required=false) String month,@RequestParam(required=false) String status){return service.jobApplications(school(a),appliedPost,subject,month,status);}
    @PostMapping("/job-config") public StaffDtos.MutationResponse saveJobConfig(Authentication a,@RequestBody Map<String,Object> body){return service.saveJobConfig(school(a),staff(a),body);}
    @PutMapping("/job-config/{id}") public StaffDtos.MutationResponse updateJobConfig(Authentication a,@PathVariable String id,@RequestBody Map<String,Object> body){return service.updateJobConfig(school(a),staff(a),id,body);}
    private static Claims claims(Authentication a){if(a==null||!(a.getDetails() instanceof Claims c))throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Authenticated claims are missing");return c;}
    private static String school(Authentication a){String v=claims(a).get("schoolId",String.class);if(v==null||v.isBlank())throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"School claim is missing");return v;}
    private static String staff(Authentication a){String v=claims(a).get("staffUid",String.class);return v==null?"":v;}
}
