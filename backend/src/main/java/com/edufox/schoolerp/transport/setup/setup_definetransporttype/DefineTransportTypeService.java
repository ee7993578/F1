package com.edufox.schoolerp.transport.setup.setup_definetransporttype;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Transport/Setup/DefineTransportType.jsx. */
@Service("page_transport_setup_setup_definetransporttype_definetransporttypeservice_java")
public class DefineTransportTypeService extends PageArtifactService<DefineTransportTypeRepository> {
    public DefineTransportTypeService(DefineTransportTypeRepository repository) {
        super(repository);
    }
}