package com.blueteam.historyEdu.responses;


import com.blueteam.historyEdu.entities.Lesson;
import com.blueteam.historyEdu.entities.Video;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LessonReponse {

    private Long lessonId;
    private List<VideoResponse> videos;
    private List<InformationResponse> infos;
    private List<QuizResponse> quizs;


    public static LessonReponse fromLesson(Lesson lesson) {
        // Handle potential null videos
        List<VideoResponse> videos = (lesson.getVideos() != null) ?
                lesson.getVideos().stream().map(VideoResponse::fromVideo).toList() :
                new ArrayList<>();

        List<InformationResponse> infos = (lesson.getInformations() != null) ?
                lesson.getInformations().stream().map(InformationResponse::fromInformation).toList() :
                new ArrayList<>();

        List<QuizResponse> quizs = (lesson.getQuizzes() != null) ?
                lesson.getQuizzes().stream().map(QuizResponse::fromQuiz).toList() :
                new ArrayList<>();

        return LessonReponse.builder()
                .lessonId(lesson.getId())
                .videos(videos)
                .infos(infos)
                .quizs(quizs)
                .build();
    }
}
