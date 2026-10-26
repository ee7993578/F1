package com.edufox.schoolerp.canteen.work.work_product_group;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Canteen/Work/Product_Group.jsx. */
@Service("page_canteen_work_work_product_group_product_groupservice_java")
public class Product_GroupService extends PageArtifactService<Product_GroupRepository> {
    public Product_GroupService(Product_GroupRepository repository) {
        super(repository);
    }
}