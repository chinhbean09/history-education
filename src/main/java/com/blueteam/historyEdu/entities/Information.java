package com.blueteam.historyEdu.entities;

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
public class Information {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "info_title")
    private String infoTitle;

    @ManyToOne
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

}
