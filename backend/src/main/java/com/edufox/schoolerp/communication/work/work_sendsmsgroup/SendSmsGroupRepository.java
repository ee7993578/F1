package com.edufox.schoolerp.communication.work.work_sendsmsgroup;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Communication/Work/SendSmsGroup.jsx. */
@Repository("page_com_edufox_schoolerp_communication_work_work_sendsmsgroup_sendsmsgrouprepository_java")
public class SendSmsGroupRepository extends PageArtifactRepository {
    public SendSmsGroupRepository() {
        super("Communication", "work", "SendSmsGroup", "Communication/Work/SendSmsGroup.jsx");
    }
}