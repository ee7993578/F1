package com.edufox.schoolerp.fee.update.update_updatestudentfee;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Update/UpdateStudentFee.jsx. */
@Repository("page_com_edufox_schoolerp_fee_update_update_updatestudentfee_updatestudentfeerepository_java")
public class UpdateStudentFeeRepository extends PageArtifactRepository {
    public UpdateStudentFeeRepository() {
        super("Fee", "update", "UpdateStudentFee", "Fee/Update/UpdateStudentFee.jsx");
    }
}