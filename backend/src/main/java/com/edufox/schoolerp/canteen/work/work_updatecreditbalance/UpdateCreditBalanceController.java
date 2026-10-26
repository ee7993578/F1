package com.edufox.schoolerp.canteen.work.work_updatecreditbalance;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Canteen/Work/UpdateCreditBalance.jsx. */
@RestController("page_canteen_work_work_updatecreditbalance_updatecreditbalancecontroller_java")
@RequestMapping("/api/page-artifacts/canteen/work/work_updatecreditbalance")
public class UpdateCreditBalanceController extends PageArtifactController<UpdateCreditBalanceService> {
    public UpdateCreditBalanceController(UpdateCreditBalanceService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}