package com.edufox.schoolerp.fee.setup.setup_defineinstallment;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Setup/DefineInstallment.jsx. */
@RestController("page_fee_setup_setup_defineinstallment_defineinstallmentcontroller_java")
@RequestMapping("/api/page-artifacts/fee/setup/setup_defineinstallment")
public class DefineInstallmentController extends PageArtifactController<DefineInstallmentService> {
    public DefineInstallmentController(DefineInstallmentService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}