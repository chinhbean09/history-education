package com.blueteam.historyEdu.services.video;

import com.blueteam.historyEdu.dtos.VideoDTO;
import com.blueteam.historyEdu.entities.*;
import com.blueteam.historyEdu.exceptions.DataNotFoundException;
import com.blueteam.historyEdu.exceptions.PermissionDenyException;
import com.blueteam.historyEdu.repositories.*;
import com.blueteam.historyEdu.responses.CourseResponse;
import com.blueteam.historyEdu.responses.VideoResponse;
import com.blueteam.historyEdu.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VideoService implements IVideoService {

    private final IVideoRepository videoRepository;
    private final IChapterRepository chapterRepository;
    private final ILessonRepository lessonRepository;
    private final ICourseRepository courseRepository;
    private final ProgressRepository progressRepository;
    private final VideoProgressRepository videoProgressRepository;

    @Override
    @Transactional
    public CourseResponse createVideo(Long lessonId, VideoDTO videoDTO) throws DataNotFoundException, PermissionDenyException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        if (currentUser.getRole().getRoleName().equals("ADMIN")) {
            // Fetch the lesson
            Lesson lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new DataNotFoundException(MessageKeys.LESSON_NOT_FOUND));

            // Create the new video entity
            Video video = videoDTO.toEntity();
            video.setLesson(lesson);
            videoRepository.save(video);
            lesson.getVideos().add(video);

            // Fetch the course associated with the lesson
            Course course = lesson.getChapter().getCourse();

            // Calculate the total duration of all videos in the course
            long totalDuration = 0L;
            for (Chapter chapter : course.getChapters()) {
                for (Lesson lessonInCourse : chapter.getLessons()) {
                    for (Video videoInLesson : lessonInCourse.getVideos()) {
                        totalDuration += videoInLesson.getDuration();
                    }
                }
            }

            // Update the course's totalDuration
            course.setTotalDuration(totalDuration);
            courseRepository.save(course);

            // Update enrolled users' progress
            updateUserProgressForNewVideo(course, video);

            // Return the updated course response
            return CourseResponse.fromCourse(course);
        } else {
            throw new PermissionDenyException(MessageKeys.PERMISSION_DENIED);
        }
    }

    private void updateUserProgressForNewVideo(Course course, Video video) {
        // Fetch all users enrolled in this course
        List<Progress> enrolledUsersProgress = progressRepository.findByCourse(course);

        // Get the chapter to which the new video's lesson belongs
        Long targetChapterId = video.getLesson().getChapter().getId();

        // Iterate through each user's progress
        for (Progress progress : enrolledUsersProgress) {
            // Check if the user's progress is associated with the chapter of the new video
            if (progress.getChapterId().equals(targetChapterId)) {
                Optional<VideoProgress> videoProgressOptional = videoProgressRepository.findByProgressAndVideo(progress, video);
                if (videoProgressOptional.isEmpty()) {
                    // Create a new VideoProgress entry for the new video
                    VideoProgress videoProgress = VideoProgress.builder()
                            .video(video)
                            .progress(progress)
                            .watchedDuration(0.0) // Initial watched duration
                            .duration(Double.valueOf(video.getDuration())) // Video duration
                            .isCompleted(false)
                            .build();

                    // Save the new VideoProgress entry
                    videoProgressRepository.save(videoProgress);
                }

                // Optionally, check if the chapter is now completed
                progress.checkChapterCompletion();
                progressRepository.save(progress);
            }
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

    @Override
    public VideoResponse getVideo(Long videoId) throws DataNotFoundException {

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.VIDEO_NOT_FOUND));
        return VideoResponse.fromVideo(video);
    }
}
