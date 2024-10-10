package com.blueteam.historyEdu.services.progress;

import com.blueteam.historyEdu.dtos.ProgressDTO;

import java.util.List;

public interface IProgressService {

     List<ProgressDTO> getProgressByUserAndCourse(Long userId, Long courseId);
     void updateProgress(Long userId, Long chapterId, ProgressDTO progressDTO);


    }
