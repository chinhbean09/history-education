    package com.blueteam.historyEdu.responses;

    import com.blueteam.historyEdu.dtos.QuestionDTO;
    import com.blueteam.historyEdu.entities.Question;
    import com.blueteam.historyEdu.entities.Quiz;
    import com.fasterxml.jackson.annotation.JsonProperty;
    import jakarta.validation.constraints.NotBlank;
    import lombok.*;

    import java.time.LocalDateTime;
    import java.util.List;
    import java.util.stream.Collectors;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class QuizResponse {

        @JsonProperty("id")
        private Long id;

        @NotBlank
        private String title;

        @JsonProperty("expiration_time")
        private int expirationTime; // có thể tính toán từ expirationTime (phút) nếu cần

        private int stt;

        @JsonProperty("lesson_id")
        private Long lessonId;

        private List<QuestionDTO> questions; // Sử dụng DTO cho câu hỏi

        public static QuizResponse fromQuiz(Quiz quiz) {
            QuizResponse response = new QuizResponse();
            response.setId(quiz.getId());
            response.setTitle(quiz.getTitle());
            response.setStt(quiz.getStt());
            response.setExpirationTime(quiz.getExpirationTime());
            response.setLessonId(quiz.getLesson().getId());

            List<QuestionDTO> questionResponses = quiz.getQuestions().stream()
                    .map(QuestionDTO::fromQuestion)
                    .collect(Collectors.toList());
            response.setQuestions(questionResponses);

            return response;
        }



    }