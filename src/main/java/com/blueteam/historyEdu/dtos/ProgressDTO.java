package com.blueteam.historyEdu.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProgressDTO {
    //private Long courseId;
    private Long chapterId;
    private boolean isChapterCompleted;
    private int chapterStt;
    private List<VideoProgressDTO> videoProgresses;
    private List<QuizProgressDTO> quizProgresses;
    private List<InfoProgressDTO> infoProgresses;
}
