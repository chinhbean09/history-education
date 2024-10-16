package com.blueteam.historyEdu.services.quiz;

import com.blueteam.historyEdu.dtos.quiz.QuizAttemptDTO;
import com.blueteam.historyEdu.dtos.quiz.QuizDTO;
import com.blueteam.historyEdu.dtos.quiz.QuizResultDTO;
import com.blueteam.historyEdu.dtos.quiz.UpdateQuizDTO;
import com.blueteam.historyEdu.entities.Quiz;
import com.blueteam.historyEdu.entities.User;
import com.blueteam.historyEdu.exceptions.DataNotFoundException;
import com.blueteam.historyEdu.responses.QuizResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IQuizService {

     List<Quiz> getAllQuizzes();

     Optional<Quiz> getQuizById(Long id) throws DataNotFoundException;

     Quiz createQuiz(QuizDTO quiz);

     QuizResponse updateQuiz(Long id, QuizDTO quizDetails) throws DataNotFoundException;

     void deleteQuiz(Long id) throws DataNotFoundException;

//     int checkAnswers(Long quizId, Map<Long, String> userAnswers);

      QuizResultDTO checkQuiz(QuizAttemptDTO quizAttemptDTO, User user);


     }
