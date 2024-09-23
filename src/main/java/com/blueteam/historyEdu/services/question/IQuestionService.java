package com.blueteam.historyEdu.services.question;

import com.blueteam.historyEdu.entities.Question;

import java.util.List;

public interface IQuestionService {
     List<Question> getAllQuestionsByQuizId(Long quizId);

    Question createQuestion(Question question);

    Question updateQuestion(Long id, Question questionDetails);

     void deleteQuestion(Long id);

    }
