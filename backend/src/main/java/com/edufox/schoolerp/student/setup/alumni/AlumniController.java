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

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student-settings/alumni")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class AlumniController {
    private final AlumniService service;
    @GetMapping public List<Map<String,Object>> list(Authentication a){Claims c=(Claims)a.getDetails();return service.list(String.valueOf(c.get("schoolId")));}
    @PostMapping public AlumniDtos.AlumniResponse save(@RequestBody AlumniDtos.AlumniRequest r,Authentication a){Claims c=(Claims)a.getDetails();return service.save(String.valueOf(c.get("schoolId")),r);}
}