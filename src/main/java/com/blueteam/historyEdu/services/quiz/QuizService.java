package com.blueteam.historyEdu.services.quiz;

import com.blueteam.historyEdu.dtos.QuestionDTO;
import com.blueteam.historyEdu.dtos.quiz.QuizAttemptDTO;
import com.blueteam.historyEdu.dtos.quiz.QuizDTO;
import com.blueteam.historyEdu.dtos.quiz.QuizResultDTO;
import com.blueteam.historyEdu.entities.*;
import com.blueteam.historyEdu.exceptions.DataNotFoundException;
import com.blueteam.historyEdu.repositories.IChapterRepository;
import com.blueteam.historyEdu.repositories.ILessonRepository;
import com.blueteam.historyEdu.repositories.IQuizAttemptRepository;
import com.blueteam.historyEdu.repositories.IQuizRepository;
import com.blueteam.historyEdu.services.question.IQuestionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuizService implements IQuizService  {
    private final IQuizRepository quizRepository;
    private final IChapterRepository chapterRepository;
    private final ILessonRepository lessonRepository;
    private final IQuizAttemptRepository quizAttemptRepository;
    private final IQuestionService questionService;

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
        Lesson lesson = lessonRepository.findById(quizDTO.getLessonId())
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found"));

        Quiz quiz = new Quiz();
        quiz.setTitle(quizDTO.getTitle());
        quiz.setExpirationTime(quizDTO.getExpirationTime());
        quiz.setStt(quizDTO.getStt());
        quiz.setLesson(lesson);

        // Create Questions from DTO
        List<Question> questions = new ArrayList<>();
        for (QuestionDTO questionDTO : quizDTO.getQuestions()) {
            Question question = new Question();
            question.setText(questionDTO.getText());
            question.setCorrectAnswer(questionDTO.getCorrectAnswer());
            question.setAnswers(questionDTO.getAnswers());
            question.setQuiz(quiz); // Set the quiz reference
            questions.add(question);
        }
        quiz.setQuestions(questions); // Add questions to the quiz

        return quizRepository.save(quiz);
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
//    @Override
//    public int checkAnswers(Long quizId, Map<Long, String> userAnswers) {
//        Quiz quiz = quizRepository.findById(quizId)
//                .orElseThrow(() -> new RuntimeException("Quiz not found"));
//
//        int correctCount = 0;
//
//        for (Question question : quiz.getQuestions()) {
//            String correctAnswer = question.getCorrectAnswer();
//            String userAnswer = userAnswers.get(question.getId());
//
//            if (correctAnswer != null && correctAnswer.equals(userAnswer)) {
//                correctCount++;
//            }
//        }
//        return correctCount; // Return the count of correct answers
//    }
@Override
public QuizResultDTO checkQuiz(QuizAttemptDTO quizAttemptDTO, User user) {
    Quiz quiz = quizRepository.findById(quizAttemptDTO.getQuizId())
            .orElseThrow(() -> new RuntimeException("Quiz not found"));

    // Lấy danh sách câu hỏi từ questionService
    List<Question> questions = questionService.getAllQuestionsByQuizId(quiz.getId());

    Map<Long, String> userAnswers = quizAttemptDTO.getAnswers();
    int totalQuestions = questions.size();
    int correctAnswers = 0;

    for (Question question : questions) {
        String userAnswer = userAnswers.get(question.getId());
        if (userAnswer != null && userAnswer.equalsIgnoreCase(question.getCorrectAnswer())) {
            correctAnswers++;
        }
    }

    // Tính tỷ lệ phần trăm điểm
    int score = (totalQuestions > 0) ? (correctAnswers * 100) / totalQuestions : 0;
    boolean isPass = score >= 80;

    // Lưu quiz attempt
    QuizAttempt quizAttempt = new QuizAttempt();
    quizAttempt.setQuiz(quiz);
    quizAttempt.setUser(user);
    quizAttempt.setAttemptDate(LocalDateTime.now());
    quizAttempt.setScore(score);
    quizAttempt.setIsPassed(isPass);

    quizAttemptRepository.save(quizAttempt);

    // Trả về kết quả
    QuizResultDTO resultDTO = new QuizResultDTO();
    resultDTO.setScore(score);
    resultDTO.setPass(isPass);

    return resultDTO;
}



}
