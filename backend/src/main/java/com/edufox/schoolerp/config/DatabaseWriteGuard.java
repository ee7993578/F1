package com.edufox.schoolerp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/** Prevents accidental writes to the read-only legacy database. */
@Component
@RequiredArgsConstructor
public class DatabaseWriteGuard {
    private final AppProperties properties;

    public void requireWritable() {
        if (properties.getAuth().isDatabaseReadOnly()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "Write operations are disabled for the read-only legacy database");
        }
    }
}
