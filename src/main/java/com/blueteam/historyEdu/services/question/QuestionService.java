package com.blueteam.historyEdu.services.question;

import com.blueteam.historyEdu.entities.Question;
import com.blueteam.historyEdu.repositories.IQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService implements  IQuestionService {
    private final IQuestionRepository questionRepository;

    @Override
    public List<Question> getAllQuestionsByQuizId(Long quizId) {
        return questionRepository.findByQuizId(quizId);
    }

    @Override
    public Question createQuestion(Question question) {
        return questionRepository.save(question);
    }

    @Override
    public Question updateQuestion(Long id, Question questionDetails) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found with id " + id));
        question.setText(questionDetails.getText());
        question.setCorrectAnswer(questionDetails.getCorrectAnswer());
        question.setAnswers(questionDetails.getAnswers());
        return questionRepository.save(question);
    }

    @Override
    public void deleteQuestion(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found with id " + id));
        questionRepository.delete(question);
    }
}
