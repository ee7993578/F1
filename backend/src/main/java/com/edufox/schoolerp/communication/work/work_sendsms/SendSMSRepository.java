package com.edufox.schoolerp.communication.work.work_sendsms;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Communication/Work/SendSMS.jsx. */
@Repository("page_com_edufox_schoolerp_communication_work_work_sendsms_sendsmsrepository_java")
public class SendSMSRepository extends PageArtifactRepository {
    public SendSMSRepository() {
        super("Communication", "work", "SendSMS", "Communication/Work/SendSMS.jsx");
    }
}