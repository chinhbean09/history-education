package com.blueteam.historyEdu.services.progress;

import com.blueteam.historyEdu.dtos.ProgressDTO;

import java.util.List;

public interface IProgressService {

     List<ProgressDTO> getProgressByUserAndChapter(Long userId, Long chapterId);
     void updateProgress(Long userId, Long chapterId, ProgressDTO progressDTO);

     List<ProgressDTO> getProgressByUserAndCourse(Long userId, Long courseId);

    }
