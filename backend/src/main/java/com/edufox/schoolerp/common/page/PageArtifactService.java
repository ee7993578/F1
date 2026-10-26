package com.edufox.schoolerp.common.page;

import lombok.RequiredArgsConstructor;

import java.util.Map;

/** Thin page service for page-owned metadata and future page-specific rules. */
@RequiredArgsConstructor
public abstract class PageArtifactService<R extends PageArtifactRepository> {
    protected final R repository;

    public Map<String, Object> descriptor() {
        return repository.descriptor();
    }
}
