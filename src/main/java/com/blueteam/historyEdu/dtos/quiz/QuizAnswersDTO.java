package com.blueteam.historyEdu.dtos.quiz;

import lombok.Data;

import java.util.Map;

@Data
public class QuizAnswersDTO {
    private Long quizId;
    private Map<Long, String> userAnswers; // Map of question ID to user's answer
}
