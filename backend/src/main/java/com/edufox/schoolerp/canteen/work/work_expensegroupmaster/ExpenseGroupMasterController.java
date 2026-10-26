package com.edufox.schoolerp.canteen.work.work_expensegroupmaster;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Canteen/Work/ExpenseGroupMaster.jsx. */
@RestController("page_canteen_work_work_expensegroupmaster_expensegroupmastercontroller_java")
@RequestMapping("/api/page-artifacts/canteen/work/work_expensegroupmaster")
public class ExpenseGroupMasterController extends PageArtifactController<ExpenseGroupMasterService> {
    public ExpenseGroupMasterController(ExpenseGroupMasterService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}