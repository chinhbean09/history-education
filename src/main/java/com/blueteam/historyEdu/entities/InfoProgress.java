package com.blueteam.historyEdu.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "info_progress")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class InfoProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "progress_id", nullable = false)
    private Progress progress;

    @Column(name = "info_id", nullable = false)
    private String infoId;

    @Column(name = "is_viewed")
    private boolean isViewed;

    @ManyToOne
    @JoinColumn(name = "information_id", nullable = false)
    private Information information;

    // Đánh dấu là đã xem
    public void markAsViewed() {
        this.isViewed = true;
    }
}
