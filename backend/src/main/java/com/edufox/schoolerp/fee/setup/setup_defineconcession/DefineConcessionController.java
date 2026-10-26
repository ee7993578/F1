package com.edufox.schoolerp.fee.setup.setup_defineconcession;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Setup/DefineConcession.jsx. */
@RestController("page_fee_setup_setup_defineconcession_defineconcessioncontroller_java")
@RequestMapping("/api/page-artifacts/fee/setup/setup_defineconcession")
public class DefineConcessionController extends PageArtifactController<DefineConcessionService> {
    public DefineConcessionController(DefineConcessionService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}