package com.blueteam.historyEdu.services.video;

import com.blueteam.historyEdu.dtos.VideoDTO;
import com.blueteam.historyEdu.entities.Chapter;
import com.blueteam.historyEdu.entities.Lesson;
import com.blueteam.historyEdu.entities.User;
import com.blueteam.historyEdu.entities.Video;
import com.blueteam.historyEdu.exceptions.DataNotFoundException;
import com.blueteam.historyEdu.exceptions.PermissionDenyException;
import com.blueteam.historyEdu.repositories.IChapterRepository;
import com.blueteam.historyEdu.repositories.ILessonRepository;
import com.blueteam.historyEdu.repositories.IVideoRepository;
import com.blueteam.historyEdu.responses.CourseResponse;
import com.blueteam.historyEdu.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VideoService implements IVideoService {

    private final IVideoRepository videoRepository;
    private final IChapterRepository chapterRepository;
    private final ILessonRepository lessonRepository;

    @Override
    @Transactional
    public CourseResponse createVideo(Long lessonId, VideoDTO videoDTO) throws DataNotFoundException, PermissionDenyException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        if (currentUser.getRole().getRoleName().equals("ADMIN")) {
            Lesson lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new DataNotFoundException(MessageKeys.LESSON_NOT_FOUND));

            Video video = videoDTO.toEntity();
            video.setLesson(lesson);
            videoRepository.save(video);
            lesson.getVideos().add(video);
            return CourseResponse.fromCourse(lesson.getChapter().getCourse());
        } else {
            throw new PermissionDenyException(MessageKeys.PERMISSION_DENIED);
        }
    }

    @Override
    @Transactional
    public CourseResponse updateVideo(Long videoId, VideoDTO videoDTO) throws DataNotFoundException, PermissionDenyException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        if (currentUser.getRole().getRoleName().equals("ADMIN")) {
            Video video = videoRepository.findById(videoId)
                    .orElseThrow(() -> new DataNotFoundException(MessageKeys.VIDEO_NOT_FOUND));
            video.setVideoName(videoDTO.getVideoName());
            video.setMoreInformation(videoDTO.getMoreInformation());
            video.setDuration(videoDTO.getDuration());
            video.setSupportingMaterials(videoDTO.getSupportingMaterials());
            video.setLessonVideo(videoDTO.getLessonVideo());
            video.setStt(videoDTO.getStt());
            videoRepository.save(video);
            return CourseResponse.fromCourse(video.getLesson().getChapter().getCourse());
        } else {
            throw new PermissionDenyException(MessageKeys.PERMISSION_DENIED);
        }
    }

    @Override
    @Transactional
    public void deleteVideo(Long videoId) throws DataNotFoundException {

        Optional<Video> video = videoRepository.findById(videoId);

        if (video.isPresent()) {
            Video videoEntity = video.get();
            Lesson lesson = videoEntity.getLesson();
            lesson.getVideos().remove(videoEntity);
            videoRepository.delete(videoEntity);
            videoRepository.flush();
            System.out.println("Video deleted: " + videoEntity.getId());
        } else {
            throw new DataNotFoundException(MessageKeys.VIDEO_NOT_FOUND);
        }
    }
}
