    package com.blueteam.historyEdu.entities;

    import com.blueteam.historyEdu.entities.common.ItemWithStt;
    import com.fasterxml.jackson.annotation.JsonBackReference;
    import com.fasterxml.jackson.annotation.JsonIgnore;
    import com.fasterxml.jackson.annotation.JsonManagedReference;
    import jakarta.persistence.*;
    import lombok.*;

    import java.time.LocalDateTime;
    import java.util.ArrayList;
    import java.util.List;

    @Entity
    @Table(name = "quizzes")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
//    @ToString(exclude = {"chapter"})  // Exclude relationships to prevent recursion
    public class Quiz implements ItemWithStt {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "title", nullable = false)
        private String title;

        @Column(name = "stt")
        private Integer stt;

        @Column(name = "expiration-time")
        private int expirationTime;

//        @ManyToOne
//        @JoinColumn(name = "chapter_id", nullable = false)
//        @JsonBackReference
//        private Chapter chapter;
        @ManyToOne
        @JoinColumn(name = "lesson_id", nullable = false)
        private Lesson lesson;

        @OneToMany(mappedBy="quiz", cascade=CascadeType.ALL, orphanRemoval=true)
        private List<QuizProgress> quizProgresses = new ArrayList<>();

        @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
        private List<Question> questions = new ArrayList<>();;

        @Override
        public void setStt(int stt) {
            this.stt = stt;
        }

        @Override
        public int getStt() {
            return stt;
        }

    }
