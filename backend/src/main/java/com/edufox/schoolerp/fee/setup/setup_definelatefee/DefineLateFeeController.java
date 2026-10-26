package com.edufox.schoolerp.fee.setup.setup_definelatefee;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Setup/DefineLateFee.jsx. */
@RestController("page_fee_setup_setup_definelatefee_definelatefeecontroller_java")
@RequestMapping("/api/page-artifacts/fee/setup/setup_definelatefee")
public class DefineLateFeeController extends PageArtifactController<DefineLateFeeService> {
    public DefineLateFeeController(DefineLateFeeService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}