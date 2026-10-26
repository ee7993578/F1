package com.edufox.schoolerp.canteen.work.work_productmaster;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Canteen/Work/ProductMaster.jsx. */
@Service("page_canteen_work_work_productmaster_productmasterservice_java")
public class ProductMasterService extends PageArtifactService<ProductMasterRepository> {
    public ProductMasterService(ProductMasterRepository repository) {
        super(repository);
    }
}