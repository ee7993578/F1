package com.edufox.schoolerp.student.setup.house_master;
import com.edufox.schoolerp.student.setup.alumni.*;
import com.edufox.schoolerp.student.setup.class_representative.*;
import com.edufox.schoolerp.student.setup.exam_configuration.*;
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
@RequestMapping("/api/student-settings/houses")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class HouseMasterController {
    private final HouseMasterService service;

    @GetMapping
    public List<Map<String,Object>> list(Authentication authentication) {
        Claims claims=(Claims)authentication.getDetails();
        return service.list(String.valueOf(claims.get("schoolId")));
    }
    @PostMapping
    public HouseMasterDtos.HouseResponse save(@RequestBody HouseMasterDtos.HouseRequest r, Authentication authentication) {
        Claims claims=(Claims)authentication.getDetails();
        return service.save(String.valueOf(claims.get("schoolId")),String.valueOf(claims.get("staffUid")),r);
    }
    @PutMapping("/{id}")
    public HouseMasterDtos.HouseResponse update(@PathVariable Integer id,@RequestBody HouseMasterDtos.HouseRequest r,Authentication authentication) {
        Claims claims=(Claims)authentication.getDetails();
        return service.save(String.valueOf(claims.get("schoolId")),String.valueOf(claims.get("staffUid")),
            new HouseMasterDtos.HouseRequest(id,r.houseName(),r.inchargeId(),r.captain(),r.viceCaptain(),r.prefect(),r.color()));
    }
    @DeleteMapping("/{id}")
    public HouseMasterDtos.HouseResponse delete(@PathVariable Integer id,Authentication authentication) {
        Claims claims=(Claims)authentication.getDetails();
        return service.delete(String.valueOf(claims.get("schoolId")),id);
    }
}