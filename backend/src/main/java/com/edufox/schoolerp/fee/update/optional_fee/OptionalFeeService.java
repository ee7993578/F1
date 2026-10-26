package com.edufox.schoolerp.fee.update.optional_fee;
import com.edufox.schoolerp.fee.report.fee_booklet.*;
import com.edufox.schoolerp.fee.report.fee_reports.*;
import com.edufox.schoolerp.fee.shared.fee_core.*;
import com.edufox.schoolerp.fee.update.fee_update.*;
import com.edufox.schoolerp.fee.work.fee_work.*;

import com.edufox.schoolerp.config.DatabaseWriteGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OptionalFeeService {
    private final OptionalFeeRepository repository;
    private final DatabaseWriteGuard writeGuard;

    public List<Map<String,Object>> load(String schoolId, String session, int feeHeadId, int sectionId, String admissionNo) {
        Integer classId = repository.classIdForSection(schoolId, sectionId);
        if (classId == null || !repository.hasCharge(schoolId, session, feeHeadId, classId)) throw new IllegalArgumentException("No charges defined");
        return repository.students(schoolId, session, sectionId, admissionNo, feeHeadId);
    }

    @Transactional
    public Map<String,Object> save(String schoolId, String staffUid, String session, int feeHeadId, List<Map<String,Object>> students) {
        writeGuard.requireWritable(); int assigned = 0, removed = 0; Integer feeType = repository.feeType(schoolId, session, feeHeadId); if (feeType == null) throw new IllegalArgumentException("Fee head not found");
        for (Map<String,Object> student : students == null ? List.<Map<String,Object>>of() : students) {
            int studentId = integer(student.get("studentId")), classId = integer(student.get("classId")); boolean checked = Boolean.parseBoolean(String.valueOf(student.getOrDefault("checked", false))); List<Integer> selected = integers(student.get("installments"));
            if (!checked) { removed += repository.deleteUnpaidStudentRows(studentId, schoolId, session, feeHeadId); continue; }
            repository.applyStreamMapping(schoolId, session, studentId, feeHeadId);
            for (int installment : selected) { BigDecimal charge = repository.charge(schoolId, session, feeHeadId, classId, feeType, installment); if (!repository.assignmentExists(schoolId, session, studentId, feeHeadId, installment)) repository.insertAssignment(studentId, feeHeadId, feeType, installment, staffUid, schoolId, session); if (charge.signum() != 0 && installment != 0) repository.insertLedgerIfMissing(studentId, installment, feeHeadId, charge, schoolId, session, classId, feeType); assigned++; }
            removed += repository.deleteUnpaidLedgerOutside(studentId, schoolId, session, feeHeadId, selected);
        }
        return Map.of("message", "Optional fee assignment saved", "assigned", assigned, "removed", removed);
    }

    private static int integer(Object value) { return value == null || String.valueOf(value).isBlank() ? 0 : Integer.parseInt(String.valueOf(value)); }
    private static List<Integer> integers(Object value) { if (!(value instanceof List<?> list)) return new ArrayList<>(); List<Integer> values = new ArrayList<>(); for (Object item : list) if (item != null && !String.valueOf(item).isBlank()) values.add(integer(item)); return values; }
}
