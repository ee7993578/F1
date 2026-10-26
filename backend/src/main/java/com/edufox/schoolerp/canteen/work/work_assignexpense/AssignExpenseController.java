package com.edufox.schoolerp.canteen.work.work_assignexpense;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Canteen/Work/AssignExpense.jsx. */
@RestController("page_canteen_work_work_assignexpense_assignexpensecontroller_java")
@RequestMapping("/api/page-artifacts/canteen/work/work_assignexpense")
public class AssignExpenseController extends PageArtifactController<AssignExpenseService> {
    public AssignExpenseController(AssignExpenseService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}