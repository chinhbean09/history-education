package com.blueteam.historyEdu.services.quizattempt;

import com.blueteam.historyEdu.entities.Quiz;
import com.blueteam.historyEdu.entities.QuizAttempt;
import com.blueteam.historyEdu.repositories.IQuizAttemptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuizAttempService implements IQuizAttemptService {
    private final IQuizAttemptRepository quizAttemptRepository;

    @Override
    public QuizAttempt attemptQuiz(Optional<Quiz> quizOpt, Integer score) {
        // Check if the quiz is present
        Quiz quiz = quizOpt.orElseThrow(() -> new RuntimeException("Quiz not found")); // or use a custom exception

        QuizAttempt attempt = QuizAttempt.builder()
                .quiz(quiz)  // Now passing a Quiz object
                .attemptDate(LocalDateTime.now())
                .score(score)
                .build();
        return quizAttemptRepository.save(attempt);
    }


    @Override
    public List<QuizAttempt> getAttemptsByUserId(Long userId) {
        return quizAttemptRepository.findByUserId(userId);
    }
}
