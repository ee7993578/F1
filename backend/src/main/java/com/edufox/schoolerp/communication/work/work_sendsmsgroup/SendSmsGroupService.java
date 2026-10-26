package com.edufox.schoolerp.communication.work.work_sendsmsgroup;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Communication/Work/SendSmsGroup.jsx. */
@Service("page_communication_work_work_sendsmsgroup_sendsmsgroupservice_java")
public class SendSmsGroupService extends PageArtifactService<SendSmsGroupRepository> {
    public SendSmsGroupService(SendSmsGroupRepository repository) {
        super(repository);
    }
}