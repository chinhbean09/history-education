package com.blueteam.historyEdu.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizProgressDTO {
    private Long quizId;
    private boolean isCompleted;
}