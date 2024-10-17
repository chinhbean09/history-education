package com.blueteam.historyEdu.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quiz_progress")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuizProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "progress_id", nullable = false)
    private Progress progress;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(name = "is_completed")
    private boolean isCompleted;

    // Đánh dấu hoàn thành quiz
    public void markAsCompleted() {
        this.isCompleted = true;
    }
}
