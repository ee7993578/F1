package com.edufox.schoolerp.fee.update.update_updatestudentfee;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Update/UpdateStudentFee.jsx. */
@RestController("page_fee_update_update_updatestudentfee_updatestudentfeecontroller_java")
@RequestMapping("/api/page-artifacts/fee/update/update_updatestudentfee")
public class UpdateStudentFeeController extends PageArtifactController<UpdateStudentFeeService> {
    public UpdateStudentFeeController(UpdateStudentFeeService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}