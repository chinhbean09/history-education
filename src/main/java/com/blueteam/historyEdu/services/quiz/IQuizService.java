package com.blueteam.historyEdu.services.quiz;

import com.blueteam.historyEdu.entities.Quiz;
import com.blueteam.historyEdu.exceptions.DataNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IQuizService {

     List<Quiz> getAllQuizzes();

     Optional<Quiz> getQuizById(Long id) throws DataNotFoundException;

     Quiz createQuiz(Quiz quiz);

     Quiz updateQuiz(Long id, Quiz quizDetails) throws DataNotFoundException;

     void deleteQuiz(Long id) throws DataNotFoundException;

     int checkAnswers(Long quizId, Map<Long, String> userAnswers);

    }
