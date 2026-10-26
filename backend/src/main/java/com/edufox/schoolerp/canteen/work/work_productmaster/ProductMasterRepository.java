package com.edufox.schoolerp.canteen.work.work_productmaster;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Canteen/Work/ProductMaster.jsx. */
@Repository("page_com_edufox_schoolerp_canteen_work_work_productmaster_productmasterrepository_java")
public class ProductMasterRepository extends PageArtifactRepository {
    public ProductMasterRepository() {
        super("Canteen", "work", "ProductMaster", "Canteen/Work/ProductMaster.jsx");
    }
}