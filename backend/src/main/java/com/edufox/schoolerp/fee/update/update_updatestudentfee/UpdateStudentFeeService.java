package com.edufox.schoolerp.fee.update.update_updatestudentfee;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Update/UpdateStudentFee.jsx. */
@Service("page_fee_update_update_updatestudentfee_updatestudentfeeservice_java")
public class UpdateStudentFeeService extends PageArtifactService<UpdateStudentFeeRepository> {
    public UpdateStudentFeeService(UpdateStudentFeeRepository repository) {
        super(repository);
    }
}