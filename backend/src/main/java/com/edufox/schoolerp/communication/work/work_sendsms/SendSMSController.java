package com.edufox.schoolerp.communication.work.work_sendsms;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Communication/Work/SendSMS.jsx. */
@RestController("page_communication_work_work_sendsms_sendsmscontroller_java")
@RequestMapping("/api/page-artifacts/communication/work/work_sendsms")
public class SendSMSController extends PageArtifactController<SendSMSService> {
    public SendSMSController(SendSMSService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}