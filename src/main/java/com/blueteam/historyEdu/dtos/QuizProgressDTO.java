package com.blueteam.historyEdu.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizProgressDTO {
    private Long quizId;
    private String quizName;
    private boolean isCompleted;
}