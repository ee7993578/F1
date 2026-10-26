package com.edufox.schoolerp.common.page;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Common page boundary used by the page-level repositories.
 *
 * The existing module repositories remain the source of truth for the SQL
 * workflow. Page repositories extend this boundary so a page can own its
 * persistence entry point without duplicating SQL that is shared by several
 * legacy pages.
 */
public abstract class PageArtifactRepository {
    private final String module;
    private final String submodule;
    private final String page;
    private final String source;

    protected PageArtifactRepository(String module, String submodule, String page, String source) {
        this.module = module;
        this.submodule = submodule;
        this.page = page;
        this.source = source;
    }

    public Map<String, Object> descriptor() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("module", module);
        result.put("submodule", submodule);
        result.put("page", page);
        result.put("source", source);
        result.put("storedProcedures", false);
        return result;
    }
}
