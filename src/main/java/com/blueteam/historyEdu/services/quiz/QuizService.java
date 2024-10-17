package com.blueteam.historyEdu.services.quiz;

import com.blueteam.historyEdu.dtos.QuestionDTO;
import com.blueteam.historyEdu.dtos.quiz.QuizAttemptDTO;
import com.blueteam.historyEdu.dtos.quiz.QuizDTO;
import com.blueteam.historyEdu.dtos.quiz.QuizResultDTO;
import com.blueteam.historyEdu.dtos.quiz.UpdateQuizDTO;
import com.blueteam.historyEdu.entities.*;
import com.blueteam.historyEdu.exceptions.DataNotFoundException;
import com.blueteam.historyEdu.repositories.*;
import com.blueteam.historyEdu.responses.QuizResponse;
import com.blueteam.historyEdu.services.question.IQuestionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class QuizService implements IQuizService  {
    private final IQuizRepository quizRepository;
    private final IChapterRepository chapterRepository;
    private final ILessonRepository lessonRepository;
    private final IQuizAttemptRepository quizAttemptRepository;
    private final IQuestionService questionService;
    private final IQuestionRepository questionRepository;

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

        List<Question> questions = new ArrayList<>();
        for (QuestionDTO questionDTO : quizDTO.getQuestions()) {
            Question question = new Question();
            question.setText(questionDTO.getText());
            question.setCorrectAnswer(questionDTO.getCorrectAnswer());
            question.setAnswers(questionDTO.getAnswers());
            question.setQuiz(quiz);
            questions.add(question);
        }
        quiz.setQuestions(questions);

        return quizRepository.save(quiz);
    }

    @Override
    @Transactional
    public QuizResponse updateQuiz(Long id, QuizDTO quizDetails) throws DataNotFoundException {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Quiz not found with id " + id));

        quiz.setTitle(quizDetails.getTitle());
        quiz.setExpirationTime(quizDetails.getExpirationTime());
        quiz.setStt(quizDetails.getStt());

// Manage the existing questions: remove those not present in the DTO
        List<Question> existingQuestions = quiz.getQuestions();
        List<Long> newQuestionIds = quizDetails.getQuestions().stream()
                .map(QuestionDTO::getId)
                .filter(Objects::nonNull) // Filter out null IDs (new questions)
                .toList();

// Remove questions that are no longer in the updated DTO
        existingQuestions.removeIf(question ->
                question.getId() != null && !newQuestionIds.contains(question.getId())
        );

// Update existing questions or add new ones
        for (QuestionDTO questionDTO : quizDetails.getQuestions()) {
            Question question = existingQuestions.stream()
                    .filter(q -> q.getId() != null && q.getId().equals(questionDTO.getId()))
                    .findFirst()
                    .orElse(null);

            if (question == null) {
                // New question
                question = new Question();
                question.setQuiz(quiz);
                existingQuestions.add(question);
            }

            question.setText(questionDTO.getText());
            question.setCorrectAnswer(questionDTO.getCorrectAnswer());
            question.setAnswers(questionDTO.getAnswers());
        }

// Save the quiz
        quizRepository.save(quiz);

        return QuizResponse.fromQuiz(quiz);

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
