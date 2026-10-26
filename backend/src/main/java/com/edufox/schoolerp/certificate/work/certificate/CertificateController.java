package com.edufox.schoolerp.certificate.work.certificate;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class CertificateController {
    private final CertificateService service;

    @GetMapping("/bootstrap")
    public Map<String,Object> bootstrap(Authentication a) { return service.bootstrap(school(claims(a))); }

    @GetMapping("/students")
    public List<Map<String,Object>> students(@RequestParam String session, @RequestParam int secId,
                                             @RequestParam(defaultValue="withdrawn") String mode, Authentication a) {
        return service.students(school(claims(a)),session,secId,mode);
    }

    @GetMapping("/fee-heads")
    public List<Map<String,Object>> feeHeads(@RequestParam String session, Authentication a) { return service.feeHeads(school(claims(a)),session); }

    @PostMapping("/bonafide")
    public Map<String,Object> bonafide(@RequestBody Map<String,Object> body, Authentication a) {
        return service.bonafide(school(claims(a)),required(body,"session"),number(body.get("studentId")),text(body.get("issueDate")));
    }

    @PostMapping("/character")
    public Map<String,Object> character(@RequestBody Map<String,Object> body, Authentication a) {
        Claims c=claims(a); return service.character(school(c),required(body,"session"),number(body.get("studentId")),text(body.get("issueDate")),text(body.get("admissionYear")),text(c.get("staffUid")));
    }

    @PostMapping("/fee")
    public Map<String,Object> fee(@RequestBody Map<String,Object> body, Authentication a) {
        String school=school(claims(a)); String session=required(body,"session");
        Map<String,Object> out=new LinkedHashMap<>(); out.put("rows",service.fee(school,session,number(body.get("secId")),number(body.get("studentId")),text(body.get("feeHeads")),text(body.get("date")),text(body.getOrDefault("reportType","Multiple")))); out.put("session",session); return out;
    }

    @PostMapping("/income")
    public Map<String,Object> income(@RequestBody Map<String,Object> body, Authentication a) { return service.income(school(claims(a)),required(body,"session"),number(body.get("secId")),number(body.get("studentId"))); }

    @GetMapping("/tc/detail")
    public Map<String,Object> tcDetail(@RequestParam String session,@RequestParam int secId,@RequestParam int studentId,Authentication a) { return service.tcDetail(school(claims(a)),session,secId,studentId); }

    @PostMapping("/tc")
    public Map<String,Object> saveTc(@RequestBody Map<String,Object> body, Authentication a) { Claims c=claims(a); return service.saveTc(school(c),text(c.get("staffUid")),body); }

    @GetMapping("/reports/{type}")
    public List<Map<String,Object>> report(@PathVariable String type,@RequestParam(required=false,defaultValue="") String session,@RequestParam(required=false,defaultValue="0") int secId,@RequestParam(required=false,defaultValue="0") int studentId,Authentication a) {
        return service.report(school(claims(a)),type,session,secId,studentId);
    }

    private static Claims claims(Authentication a) { if(a==null || !(a.getDetails() instanceof Claims c)) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Authenticated claims are missing"); return c; }
    private static String school(Claims c) { String v=c.get("schoolId",String.class); if(v==null||v.isBlank()) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"School claim is missing"); return v; }
    private static String text(Object v) { return v==null?"":String.valueOf(v).trim(); }
    private static int number(Object v) { try{return Integer.parseInt(text(v));}catch(Exception e){return 0;} }
    private static String required(Map<String,Object> b,String key) { String v=text(b.get(key)); if(v.isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,key+" is required"); return v; }
}
