package com.blueteam.historyEdu.services.chapter;

import com.blueteam.historyEdu.dtos.ChapterDTO;
import com.blueteam.historyEdu.exceptions.DataNotFoundException;
import com.blueteam.historyEdu.exceptions.PermissionDenyException;
import com.blueteam.historyEdu.responses.CourseResponse;

public interface IChapterService {

    CourseResponse createChapter(Long courseId, ChapterDTO chapterDTO) throws DataNotFoundException, PermissionDenyException;

    CourseResponse updateChapter(Long chapterId, ChapterDTO chapterDTO) throws DataNotFoundException, PermissionDenyException;

    void deleteChapter(Long chapterId) throws DataNotFoundException;
}
