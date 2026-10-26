package com.edufox.schoolerp.master.work.work_change_section_name;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for MasterSetting/Work/Change_Section_Name.jsx. */
@RestController("page_master_work_work_change_section_name_change_section_namecontroller_java")
@RequestMapping("/api/page-artifacts/mastersetting/work/work_change_section_name")
public class Change_Section_NameController extends PageArtifactController<Change_Section_NameService> {
    public Change_Section_NameController(Change_Section_NameService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}