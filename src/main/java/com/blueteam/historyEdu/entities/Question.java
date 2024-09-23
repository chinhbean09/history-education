package com.blueteam.historyEdu.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@ToString(exclude = {"quiz"})  // Exclude relationships to prevent recursion
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "correct_answer", nullable = false)
    private String correctAnswer;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "question_answers", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "answer")
    private List<String> answers;



}
