package com.blueteam.historyEdu.controllers;

import com.blueteam.historyEdu.dtos.quiz.QuizAttemptDTO;
import com.blueteam.historyEdu.dtos.quiz.QuizDTO;
import com.blueteam.historyEdu.dtos.quiz.QuizResultDTO;
import com.blueteam.historyEdu.dtos.quiz.UpdateQuizDTO;
import com.blueteam.historyEdu.entities.Quiz;
import com.blueteam.historyEdu.entities.User;
import com.blueteam.historyEdu.exceptions.DataNotFoundException;
import com.blueteam.historyEdu.responses.QuizResponse;
import com.blueteam.historyEdu.services.lesson.ILessonService;
import com.blueteam.historyEdu.services.quiz.IQuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final IQuizService quizService;
    private final ILessonService lessonService;

    @GetMapping("/get-all-quizzes")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @Transactional
    public List<QuizResponse> getAllQuizzes() {
        List<Quiz> quizzes = quizService.getAllQuizzes();
        return quizzes.stream()
                .map(QuizResponse::fromQuiz)
                .collect(Collectors.toList());
    }


    @GetMapping("/get-quiz-by-id/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CUSTOMER')")
    @Transactional
    public ResponseEntity<QuizResponse> getQuizById(@PathVariable Long id) throws DataNotFoundException {
        Optional<Quiz> quizOptional = quizService.getQuizById(id);

        return quizOptional.map(quiz -> {
            QuizResponse quizResponse = QuizResponse.fromQuiz(quiz);
            return ResponseEntity.ok(quizResponse);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping("/create-quiz")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> createQuiz(@RequestBody QuizDTO quiz) {
        Quiz createdQuiz = quizService.createQuiz(quiz);

        QuizResponse quizResponse = QuizResponse.fromQuiz(createdQuiz);
        return ResponseEntity.status(HttpStatus.CREATED).body("Quiz created successfully: " + createdQuiz.getTitle());

    }

    @PutMapping("/update-quiz/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<QuizResponse> updateQuiz(@PathVariable Long id, @RequestBody QuizDTO quizDetails) throws DataNotFoundException {
        QuizResponse updatedQuiz = quizService.updateQuiz(id, quizDetails);
        return ResponseEntity.ok(updatedQuiz);
    }
    @DeleteMapping("/delete-quiz/{lessonId}/{quizId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long lessonId, @PathVariable Long quizId) throws DataNotFoundException {
        lessonService.deleteQuizAndUpdateStt(lessonId, quizId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/check-answers")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CUSTOMER')")
    public ResponseEntity<QuizResultDTO> checkQuiz(@RequestBody QuizAttemptDTO quizAttemptDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        QuizResultDTO resultDTO = quizService.checkQuiz(quizAttemptDTO, currentUser);
        return ResponseEntity.ok(resultDTO);
    }

}
