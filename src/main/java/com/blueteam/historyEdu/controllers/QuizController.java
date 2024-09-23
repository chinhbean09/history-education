package com.blueteam.historyEdu.controllers;

import com.blueteam.historyEdu.dtos.quiz.QuizAnswersDTO;
import com.blueteam.historyEdu.entities.Quiz;
import com.blueteam.historyEdu.exceptions.DataNotFoundException;
import com.blueteam.historyEdu.services.quiz.IQuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final IQuizService quizService;

    @GetMapping("/get-all-quizzes")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public List<Quiz> getAllQuizzes() {
        return quizService.getAllQuizzes();
    }

    @GetMapping("/get-quiz-by-id/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<Quiz> getQuizById(@PathVariable Long id) throws DataNotFoundException {
        Optional<Quiz> quiz = quizService.getQuizById(id);
        return quiz.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create-quiz")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<Quiz> createQuiz(@RequestBody Quiz quiz) {
        Quiz createdQuiz = quizService.createQuiz(quiz);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdQuiz);
    }

    @PutMapping("/update-quiz/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<Quiz> updateQuiz(@PathVariable Long id, @RequestBody Quiz quizDetails) throws DataNotFoundException {
        Quiz updatedQuiz = quizService.updateQuiz(id, quizDetails);
        return ResponseEntity.ok(updatedQuiz);
    }
    @DeleteMapping("/delete-quiz/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) throws DataNotFoundException {
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/check-answers")
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<Integer> checkAnswers(@RequestBody QuizAnswersDTO quizAnswersDTO) {
        int correctCount = quizService.checkAnswers(quizAnswersDTO.getQuizId(), quizAnswersDTO.getUserAnswers());
        return ResponseEntity.ok(correctCount);
    }

}
