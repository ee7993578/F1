package com.edufox.schoolerp.student;

import com.edufox.schoolerp.config.DatabaseWriteGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentImageService {
    private final StudentImageRepository repository;
    private final DatabaseWriteGuard writeGuard;
    @Transactional
    public StudentImageDtos.ImageResponse save(String schoolId, String uid, StudentImageDtos.ImageRequest request) {
        writeGuard.requireWritable();
        return repository.save(schoolId, uid, request);
    }
}
