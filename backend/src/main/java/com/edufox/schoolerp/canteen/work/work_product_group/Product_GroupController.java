package com.edufox.schoolerp.canteen.work.work_product_group;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Canteen/Work/Product_Group.jsx. */
@RestController("page_canteen_work_work_product_group_product_groupcontroller_java")
@RequestMapping("/api/page-artifacts/canteen/work/work_product_group")
public class Product_GroupController extends PageArtifactController<Product_GroupService> {
    public Product_GroupController(Product_GroupService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}