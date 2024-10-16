package com.blueteam.historyEdu.controllers;

import com.blueteam.historyEdu.entities.Quiz;
import com.blueteam.historyEdu.entities.QuizAttempt;
import com.blueteam.historyEdu.entities.User;
import com.blueteam.historyEdu.exceptions.DataNotFoundException;
import com.blueteam.historyEdu.services.quiz.IQuizService; // Assuming you have this service
import com.blueteam.historyEdu.services.quizattempt.IQuizAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/quiz-attempts")
@RequiredArgsConstructor
public class QuizAttemptController {

    private final IQuizAttemptService quizAttemptService;
    private final IQuizService quizService; // Service to get quiz details

    @PostMapping("/attempt/{quizId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CUSTOMER')")
    public ResponseEntity<QuizAttempt> attemptQuiz(@PathVariable Long quizId, @RequestBody Integer score) throws DataNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Optional<Quiz> quiz = quizService.getQuizById(quizId);
        if(quiz == null) return ResponseEntity.notFound().build();
        QuizAttempt attempt = quizAttemptService.attemptQuiz(quiz, score);
        attempt.setUser(currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(attempt);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CUSTOMER')")
    public ResponseEntity<List<QuizAttempt>> getAttemptsByUser(@PathVariable Long userId) {
        List<QuizAttempt> attempts = quizAttemptService.getAttemptsByUserId(userId);
        return ResponseEntity.ok(attempts);
    }
}
