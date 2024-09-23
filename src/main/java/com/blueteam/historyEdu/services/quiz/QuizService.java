package com.blueteam.historyEdu.services.quiz;

import com.blueteam.historyEdu.dtos.quiz.QuizDTO;
import com.blueteam.historyEdu.entities.Chapter;
import com.blueteam.historyEdu.entities.Question;
import com.blueteam.historyEdu.entities.Quiz;
import com.blueteam.historyEdu.exceptions.DataNotFoundException;
import com.blueteam.historyEdu.repositories.IChapterRepository;
import com.blueteam.historyEdu.repositories.IQuizRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuizService implements IQuizService  {
    private final IQuizRepository quizRepository;
    private final IChapterRepository chapterRepository;
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
    public Quiz createQuiz(QuizDTO quizDTO) {
        Chapter chapter = chapterRepository.findById(quizDTO.getChapterId())
                .orElseThrow(() -> new EntityNotFoundException("Chapter not found"));

        Quiz quiz = new Quiz();
        quiz.setTitle(quizDTO.getTitle());
        quiz.setExpirationTime(quizDTO.getExpirationTime()); // Lưu ý đây là thời gian phút
        quiz.setChapter(chapter);

        // Liên kết câu hỏi với quiz
        List<Question> questions = quizDTO.getQuestions();
        for (Question question : questions) {
            question.setQuiz(quiz); // Thiết lập mối quan hệ hai chiều
        }
        quiz.setQuestions(questions); // Đặt danh sách câu hỏi vào quiz

        return quizRepository.save(quiz); // Lưu quiz cùng với các câu hỏi
    }

    @Override
    public Quiz updateQuiz(Long id, Quiz quizDetails) throws DataNotFoundException {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Quiz not found with id " + id));
        quiz.setTitle(quizDetails.getTitle());
        quiz.setExpirationTime(quizDetails.getExpirationTime());
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
