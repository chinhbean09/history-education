package com.blueteam.historyEdu.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizProgressDTO {
    private String quizId;
    private boolean isCompleted;
}