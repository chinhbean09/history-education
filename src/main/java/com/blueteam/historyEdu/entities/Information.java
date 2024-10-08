package com.blueteam.historyEdu.entities;

import com.blueteam.historyEdu.entities.common.ItemWithStt;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "informations")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Information implements ItemWithStt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "info_title")
    private String infoTitle;

    @Column(name = "content")
    private String content;

    @Column(name = "stt")
    private Integer stt;

    @ManyToOne
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Override
    public void setStt(int stt) {
        this.stt = stt;
    }

    @Override
    public int getStt() {
        return stt;
    }

}
