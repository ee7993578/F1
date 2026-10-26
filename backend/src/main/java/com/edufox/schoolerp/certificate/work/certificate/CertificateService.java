package com.edufox.schoolerp.certificate.work.certificate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CertificateService {
    private final CertificateRepository repository;

    public Map<String,Object> bootstrap(String school) { return repository.bootstrap(school); }
    public List<Map<String,Object>> students(String school, String session, int secId, String mode) { return repository.students(school,session,secId,mode); }
    public Map<String,Object> bonafide(String school, String session, int studentId, String issueDate) { return repository.bonafide(school,session,studentId,issueDate); }
    public Map<String,Object> character(String school, String session, int studentId, String issueDate, String admissionYear, String staff) { return repository.character(school,session,studentId,issueDate,admissionYear,staff); }
    public List<Map<String,Object>> feeHeads(String school, String session) { return repository.feeHeads(school,session); }
    public List<Map<String,Object>> fee(String school, String session, int secId, int studentId, String selectedHeads, String date, String reportType) { return repository.fee(school,session,secId,studentId,selectedHeads,date,reportType); }
    public Map<String,Object> income(String school, String session, int secId, int studentId) { return repository.income(school,session,secId,studentId); }
    public Map<String,Object> tcDetail(String school, String session, int secId, int studentId) { return repository.tcDetail(school,session,secId,studentId); }
    public Map<String,Object> saveTc(String school, String staff, Map<String,Object> body) { return repository.saveTc(school,staff,body); }
    public List<Map<String,Object>> report(String school, String type, String session, int secId, int studentId) { return repository.report(school,type,session,secId,studentId); }
}
