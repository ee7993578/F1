package com.edufox.schoolerp.student;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentImageService {
    private final StudentImageRepository repository;
    @Transactional
    public StudentImageDtos.ImageResponse save(String schoolId, String uid, StudentImageDtos.ImageRequest request) {
        return repository.save(schoolId, uid, request);
    }
}