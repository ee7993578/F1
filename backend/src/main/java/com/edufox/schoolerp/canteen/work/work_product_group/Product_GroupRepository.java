package com.edufox.schoolerp.canteen.work.work_product_group;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Canteen/Work/Product_Group.jsx. */
@Repository("page_com_edufox_schoolerp_canteen_work_work_product_group_product_grouprepository_java")
public class Product_GroupRepository extends PageArtifactRepository {
    public Product_GroupRepository() {
        super("Canteen", "work", "Product_Group", "Canteen/Work/Product_Group.jsx");
    }
}