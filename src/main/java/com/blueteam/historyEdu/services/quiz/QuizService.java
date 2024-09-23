package com.blueteam.historyEdu.services.quiz;

import com.blueteam.historyEdu.entities.Question;
import com.blueteam.historyEdu.entities.Quiz;
import com.blueteam.historyEdu.exceptions.DataNotFoundException;
import com.blueteam.historyEdu.repositories.IQuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuizService implements IQuizService  {
    private final IQuizRepository quizRepository;

    @Override
    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }
    @Override
    public Optional<Quiz> getQuizById(Long id) throws DataNotFoundException {
        return Optional.ofNullable(quizRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Quiz not found with id " + id)));
    }
    @Override
    public Quiz createQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }
    @Override
    public Quiz updateQuiz(Long id, Quiz quizDetails) throws DataNotFoundException {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Quiz not found with id " + id));
        quiz.setPassword(quizDetails.getPassword());
        quiz.setQuestion(quizDetails.getQuestion());
        quiz.setExpirationTime(quizDetails.getExpirationTime());
        quiz.setVerified(quizDetails.isVerified());
        return quizRepository.save(quiz);
    }
    @Override
    public void deleteQuiz(Long id) throws DataNotFoundException {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Quiz not found with id " + id));
        quizRepository.delete(quiz);
    }
    @Override
    public int checkAnswers(Long quizId, Map<Long, String> userAnswers) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        int correctCount = 0;

        for (Question question : quiz.getQuestions()) {
            String correctAnswer = question.getCorrectAnswer();
            String userAnswer = userAnswers.get(question.getId());

            if (correctAnswer != null && correctAnswer.equals(userAnswer)) {
                correctCount++;
            }
        }
        return correctCount; // Return the count of correct answers
    }


}
