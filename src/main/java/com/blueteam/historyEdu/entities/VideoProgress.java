package com.blueteam.historyEdu.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "video_progress")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "progress_id", nullable = false)
    private Progress progress;

    @ManyToOne
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @Column(name = "watched_duration")
    private Double watchedDuration;

    @Column(name = "duration")
    private Double duration;

    @Column(name = "is_completed")
    private boolean isCompleted;

    // Cập nhật watchedDuration và tự động tính toán isCompleted
    public void updateWatchedDuration(double newWatchedDuration) {
        this.watchedDuration = newWatchedDuration;
        this.isCompleted = (this.watchedDuration / this.duration) >= 0.8;
    }
}
