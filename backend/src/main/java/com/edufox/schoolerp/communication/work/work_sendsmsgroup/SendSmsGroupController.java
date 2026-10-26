package com.edufox.schoolerp.communication.work.work_sendsmsgroup;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Communication/Work/SendSmsGroup.jsx. */
@RestController("page_communication_work_work_sendsmsgroup_sendsmsgroupcontroller_java")
@RequestMapping("/api/page-artifacts/communication/work/work_sendsmsgroup")
public class SendSmsGroupController extends PageArtifactController<SendSmsGroupService> {
    public SendSmsGroupController(SendSmsGroupService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}