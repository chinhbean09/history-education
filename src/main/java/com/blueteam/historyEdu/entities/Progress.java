package com.blueteam.historyEdu.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "progress")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Progress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "chapter_id", nullable = false)
    private Long chapterId;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;  // Thêm quan hệ với Course

    @Column(name = "is_chapter_completed")
    private boolean isChapterCompleted;

    @OneToMany(mappedBy = "progress", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<VideoProgress> videoProgresses;

    @OneToMany(mappedBy = "progress", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<QuizProgress> quizProgresses;

    @OneToMany(mappedBy = "progress", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<InfoProgress> infoProgresses;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    // Kiểm tra xem tất cả các video, quiz, và info đã hoàn thành hay chưa
    public void checkChapterCompletion() {
        boolean allVideosCompleted = videoProgresses.stream().allMatch(VideoProgress::isCompleted);
        boolean allQuizzesCompleted = quizProgresses.stream().allMatch(QuizProgress::isCompleted);
        boolean allInfosViewed = infoProgresses.stream().allMatch(InfoProgress::isViewed);

        this.isChapterCompleted = allVideosCompleted && allQuizzesCompleted && allInfosViewed;
    }
}
