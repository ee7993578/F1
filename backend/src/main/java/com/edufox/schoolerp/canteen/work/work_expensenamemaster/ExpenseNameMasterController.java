package com.edufox.schoolerp.canteen.work.work_expensenamemaster;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Canteen/Work/ExpenseNameMaster.jsx. */
@RestController("page_canteen_work_work_expensenamemaster_expensenamemastercontroller_java")
@RequestMapping("/api/page-artifacts/canteen/work/work_expensenamemaster")
public class ExpenseNameMasterController extends PageArtifactController<ExpenseNameMasterService> {
    public ExpenseNameMasterController(ExpenseNameMasterService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}