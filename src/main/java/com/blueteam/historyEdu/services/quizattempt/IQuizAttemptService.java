package com.blueteam.historyEdu.services.quizattempt;

import com.blueteam.historyEdu.entities.Quiz;
import com.blueteam.historyEdu.entities.QuizAttempt;

import java.util.List;
import java.util.Optional;

public interface IQuizAttemptService {

     QuizAttempt attemptQuiz(Optional<Quiz> quiz, Integer score);

     List<QuizAttempt> getAttemptsByUserId(Long userId);


    }
