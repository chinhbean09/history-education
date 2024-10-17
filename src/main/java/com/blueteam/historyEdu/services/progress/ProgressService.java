package com.blueteam.historyEdu.services.progress;

import com.blueteam.historyEdu.dtos.InfoProgressDTO;
import com.blueteam.historyEdu.dtos.ProgressDTO;
import com.blueteam.historyEdu.dtos.QuizProgressDTO;
import com.blueteam.historyEdu.dtos.VideoProgressDTO;
import com.blueteam.historyEdu.entities.*;
import com.blueteam.historyEdu.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressService implements IProgressService{
    private final ProgressRepository progressRepository;

    private final IUserRepository userRepository;
    private final IVideoRepository videoRepository;

    private final VideoProgressRepository videoProgressRepository;

    private final QuizProgressRepository quizProgressRepository;

    private final InfoProgressRepository infoProgressRepository;

    private final IQuizRepository quizRepository;

    public List<ProgressDTO> getProgressByUserAndCourse(Long userId, Long courseId) {
        List<Progress> progressList = progressRepository.findByUserIdAndCourseId(userId, courseId);
        return progressList.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional
    public void updateProgress(Long userId, Long chapterId, ProgressDTO progressDTO) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            Progress progress = progressRepository.findByUserIdAndChapterId(userId, chapterId);

            if (progress == null) {
                progress = new Progress();
                progress.setUser(user);
                progress.setChapterId(chapterId);
            }

            updateProgressFromDto(progress, progressDTO);
            progressRepository.save(progress);
        } else {
            throw new RuntimeException("User not found with id: " + userId);
        }
    }

    private ProgressDTO convertToDto(Progress progress) {
        ProgressDTO dto = new ProgressDTO();
        dto.setChapterId(progress.getChapterId());
        dto.setChapterCompleted(progress.isChapterCompleted());

        List<VideoProgressDTO> videoProgressDTOs = progress.getVideoProgresses().stream().map(videoProgress -> {
            VideoProgressDTO videoDTO = new VideoProgressDTO();
            videoDTO.setVideoId(videoProgress.getVideo().getId());
            videoDTO.setVideoName(videoProgress.getVideo().getVideoName());  // Thêm videoName vào DTO
            videoDTO.setWatchedDuration(videoProgress.getWatchedDuration());
            videoDTO.setDuration(videoProgress.getDuration());
            videoDTO.setCompleted(videoProgress.isCompleted());
            return videoDTO;
        }).collect(Collectors.toList());
        dto.setVideoProgresses(videoProgressDTOs);

        List<QuizProgressDTO> quizProgressDTOs = progress.getQuizProgresses().stream().map(quizProgress -> {
            QuizProgressDTO quizDTO = new QuizProgressDTO();
            quizDTO.setQuizId(quizProgress.getQuiz().getId());
            quizDTO.setQuizName(quizProgress.getQuiz().getTitle()); // Thêm quizName vào DTO
            quizDTO.setCompleted(quizProgress.isCompleted());
            return quizDTO;
        }).collect(Collectors.toList());
        dto.setQuizProgresses(quizProgressDTOs);

        List<InfoProgressDTO> infoProgressDTOs = progress.getInfoProgresses().stream().map(infoProgress -> {
            InfoProgressDTO infoDTO = new InfoProgressDTO();
            infoDTO.setInfoId(infoProgress.getInfoId());
            infoDTO.setInfoName(infoProgress.getInformation().getInfoTitle()); // Thêm infoName vào DTO
            infoDTO.setViewed(infoProgress.isViewed());
            return infoDTO;
        }).collect(Collectors.toList());
        dto.setInfoProgresses(infoProgressDTOs);

        return dto;
    }


    private void updateProgressFromDto(Progress progress, ProgressDTO progressDTO) {
        progress.setChapterCompleted(progressDTO.isChapterCompleted());

        for (VideoProgressDTO videoDTO : progressDTO.getVideoProgresses()) {
            Optional<Video> videoOptional = videoRepository.findById(videoDTO.getVideoId());
            if (videoOptional.isPresent()) {
                Video video = videoOptional.get();

                VideoProgress videoProgress = videoProgressRepository.findByProgressAndVideoId(progress, video.getId());
                if (videoProgress == null) {
                    videoProgress = new VideoProgress();
                    videoProgress.setProgress(progress);
                    videoProgress.setVideo(video);
                }

                videoProgress.setWatchedDuration(videoDTO.getWatchedDuration());
                videoProgress.setDuration(videoDTO.getDuration());
                videoProgress.setCompleted(videoDTO.isCompleted());

                videoProgressRepository.save(videoProgress);
            }
        }

        for (QuizProgressDTO quizDTO : progressDTO.getQuizProgresses()) {
            QuizProgress quizProgress = quizProgressRepository.findByProgressAndQuizId(progress, quizDTO.getQuizId());
            if (quizProgress == null) {
                Long quiz = quizDTO.getQuizId();
                Optional<Quiz> quiz1 = quizRepository.findById(quiz);
                quizProgress = new QuizProgress();
                quizProgress.setProgress(progress);
                quizProgress.setQuiz(quiz1.get());
            }
            quizProgress.setCompleted(quizDTO.isCompleted());
            quizProgressRepository.save(quizProgress);
        }
        for (InfoProgressDTO infoDTO : progressDTO.getInfoProgresses()) {
            InfoProgress infoProgress = infoProgressRepository.findByProgressAndInfoId(progress, infoDTO.getInfoId());
            if (infoProgress == null) {
                infoProgress = new InfoProgress();
                infoProgress.setProgress(progress);
                infoProgress.setInfoId(infoDTO.getInfoId());
            }
            infoProgress.setViewed(infoDTO.isViewed());
            infoProgressRepository.save(infoProgress);
        }

        // Check if all videos are completed, all quizzes are completed, and all info is viewed
        boolean allVideosCompleted = progress.getVideoProgresses().stream()
                .allMatch(VideoProgress::isCompleted);

        boolean allQuizzesCompleted = progress.getQuizProgresses().stream()
                .allMatch(QuizProgress::isCompleted);

        boolean allInfoViewed = progress.getInfoProgresses().stream()
                .allMatch(InfoProgress::isViewed);

        // If all are true, set chapterCompleted to true
        if (allVideosCompleted && allQuizzesCompleted && allInfoViewed) {
            progress.setChapterCompleted(true);
        } else {
            progress.setChapterCompleted(false); // Optionally reset if not all are complete
        }
    }

}