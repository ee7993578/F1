package com.edufox.schoolerp.visitor;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/visitor")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class VisitorController {
    private final VisitorService service;
    private static Claims claims(Authentication a) { return (Claims)a.getDetails(); }
    private static String claim(Authentication a,String key) { return String.valueOf(claims(a).getOrDefault(key,"")); }
    private static Integer userId(Authentication a) { try{return Integer.valueOf(claim(a,"userId"));}catch(Exception e){return null;} }

    @GetMapping("/bootstrap") public Map<String,Object> bootstrap(Authentication a){return service.bootstrap(claim(a,"schoolId"));}
    @GetMapping("/config") public Map<String,Object> config(Authentication a){return service.config(claim(a,"schoolId"));}
    @PutMapping("/config") public Map<String,Object> saveConfig(@RequestBody Map<String,Object> body,Authentication a){return service.saveConfig(claim(a,"schoolId"),userId(a),body);}
    @GetMapping("/visitors/search") public Map<String,Object> search(@RequestParam(defaultValue="") String term,Authentication a){return Map.of("rows",service.searchVisitors(claim(a,"schoolId"),term));}
    @GetMapping("/appointments") public Map<String,Object> appointments(@RequestParam(defaultValue="false") boolean today,Authentication a){return service.appointments(claim(a,"schoolId"),today);}
    @GetMapping("/appointments/{id}") public Map<String,Object> appointment(@PathVariable int id,Authentication a){return service.appointment(claim(a,"schoolId"),id);}
    @PostMapping("/appointments") public Map<String,Object> save(@RequestBody Map<String,Object> body,Authentication a){return service.saveAppointment(claim(a,"schoolId"),userId(a),body,null);}
    @PutMapping("/appointments/{id}") public Map<String,Object> update(@PathVariable int id,@RequestBody Map<String,Object> body,Authentication a){return service.saveAppointment(claim(a,"schoolId"),userId(a),body,id);}
    @PostMapping("/appointments/{id}/cancel") public Map<String,Object> cancel(@PathVariable int id,@RequestBody Map<String,Object> body,Authentication a){return service.cancel(claim(a,"schoolId"),id,String.valueOf(body.getOrDefault("reason","")));}
    @GetMapping("/approval") public Map<String,Object> approval(@RequestParam(defaultValue="") String search,@RequestParam(defaultValue="") String visitorTypeId,@RequestParam(defaultValue="") String departmentId,@RequestParam(defaultValue="") String visitDate,@RequestParam(defaultValue="Pending") String status,Authentication a){return service.approval(claim(a,"schoolId"),search,intValue(visitorTypeId),intValue(departmentId),visitDate,status);}
    @PostMapping("/approval/{id}") public Map<String,Object> setApproval(@PathVariable int id,@RequestBody Map<String,Object> body,Authentication a){return service.setApproval(claim(a,"schoolId"),userId(a),id,String.valueOf(body.getOrDefault("status","")),String.valueOf(body.getOrDefault("reason","")));}
    @GetMapping("/history") public Map<String,Object> history(@RequestParam(defaultValue="") String from,@RequestParam(defaultValue="") String to,@RequestParam(defaultValue="") String status,@RequestParam(defaultValue="") String search,Authentication a){return service.history(claim(a,"schoolId"),from,to,status,search);}
    @GetMapping("/history/{id}") public Map<String,Object> details(@PathVariable int id,Authentication a){return service.details(claim(a,"schoolId"),id);}
    @GetMapping("/dashboard") public Map<String,Object> dashboard(Authentication a){return service.dashboard(claim(a,"schoolId"));}
    @GetMapping("/dashboard/appointment") public Map<String,Object> lookup(@RequestParam(defaultValue="") String refNo,Authentication a){return service.lookupAppointment(claim(a,"schoolId"),refNo);}
    private static Integer intValue(String value){try{int x=Integer.parseInt(value);return x>0?x:null;}catch(Exception e){return null;}}
}
