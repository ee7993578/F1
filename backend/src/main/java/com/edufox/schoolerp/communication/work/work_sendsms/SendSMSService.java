package com.edufox.schoolerp.communication.work.work_sendsms;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Communication/Work/SendSMS.jsx. */
@Service("page_communication_work_work_sendsms_sendsmsservice_java")
public class SendSMSService extends PageArtifactService<SendSMSRepository> {
    public SendSMSService(SendSMSRepository repository) {
        super(repository);
    }
}