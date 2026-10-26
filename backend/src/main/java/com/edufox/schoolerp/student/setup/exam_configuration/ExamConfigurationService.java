package com.edufox.schoolerp.student.setup.exam_configuration;
import com.edufox.schoolerp.student.setup.alumni.*;
import com.edufox.schoolerp.student.setup.class_representative.*;
import com.edufox.schoolerp.student.setup.house_master.*;
import com.edufox.schoolerp.student.setup.registration_open.*;
import com.edufox.schoolerp.student.shared.student_core.*;
import com.edufox.schoolerp.student.update.student_image.*;
import com.edufox.schoolerp.student.update.student_mutation.*;
import com.edufox.schoolerp.student.update.student_update.*;
import com.edufox.schoolerp.student.work.bulk_admission.*;
import com.edufox.schoolerp.student.work.class_allotment.*;
import com.edufox.schoolerp.student.work.online_registration.*;
import com.edufox.schoolerp.student.work.registration_redirection.*;
import com.edufox.schoolerp.student.work.student_admission.*;
import com.edufox.schoolerp.student.work.student_registration.*;
import com.edufox.schoolerp.student.work.student_work.*;

import com.edufox.schoolerp.config.DatabaseWriteGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExamConfigurationService {
    private final ExamConfigurationRepository repository;
    private final DatabaseWriteGuard writeGuard;
    public List<Map<String,Object>> list(String schoolId,String session){return repository.list(schoolId,session);}
    public Map<String,Object> options(){return repository.options();}
    @Transactional public ExamConfigurationDtos.ExamResponse save(String schoolId,String staffUid,ExamConfigurationDtos.ExamRequest r){writeGuard.requireWritable();return repository.save(schoolId,staffUid,r);}
    @Transactional public ExamConfigurationDtos.ExamResponse delete(String schoolId,Integer id){writeGuard.requireWritable();return repository.delete(schoolId,id);}
}
