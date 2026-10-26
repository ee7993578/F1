package com.edufox.schoolerp.fee.setup.setup_defineconcessionvalue;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Setup/DefineConcessionValue.jsx. */
@RestController("page_fee_setup_setup_defineconcessionvalue_defineconcessionvaluecontroller_java")
@RequestMapping("/api/page-artifacts/fee/setup/setup_defineconcessionvalue")
public class DefineConcessionValueController extends PageArtifactController<DefineConcessionValueService> {
    public DefineConcessionValueController(DefineConcessionValueService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}