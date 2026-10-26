package com.edufox.schoolerp.canteen.work.work_productmaster;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Canteen/Work/ProductMaster.jsx. */
@RestController("page_canteen_work_work_productmaster_productmastercontroller_java")
@RequestMapping("/api/page-artifacts/canteen/work/work_productmaster")
public class ProductMasterController extends PageArtifactController<ProductMasterService> {
    public ProductMasterController(ProductMasterService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}