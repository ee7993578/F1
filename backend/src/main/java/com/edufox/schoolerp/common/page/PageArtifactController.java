package com.edufox.schoolerp.common.page;

import lombok.RequiredArgsConstructor;

import java.util.Map;

/** Shared HTTP adapter for page-level controllers. */
@RequiredArgsConstructor
public abstract class PageArtifactController<S extends PageArtifactService<?>> {
    protected final S service;

    public Map<String, Object> descriptor() {
        return service.descriptor();
    }
}
