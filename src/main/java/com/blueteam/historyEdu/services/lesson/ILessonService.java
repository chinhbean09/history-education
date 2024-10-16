package com.blueteam.historyEdu.services.lesson;

import com.blueteam.historyEdu.exceptions.DataNotFoundException;

public interface ILessonService {

     void deleteQuizAndUpdateStt(Long lessonId, Long quizId) throws DataNotFoundException;

    void deleteVideoAndUpdateStt(Long lessonId, Long videoId) throws DataNotFoundException;

    void deleteInfoAndUpdateStt(Long lessonId, Long infoId) throws DataNotFoundException;

}
