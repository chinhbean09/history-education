package com.blueteam.historyEdu.services.lesson;

import com.blueteam.historyEdu.entities.Information;
import com.blueteam.historyEdu.entities.Lesson;
import com.blueteam.historyEdu.entities.Quiz;
import com.blueteam.historyEdu.entities.Video;
import com.blueteam.historyEdu.entities.common.ItemWithStt;
import com.blueteam.historyEdu.exceptions.DataNotFoundException;
import com.blueteam.historyEdu.repositories.IInformationRepository;
import com.blueteam.historyEdu.repositories.ILessonRepository;
import com.blueteam.historyEdu.repositories.IQuizRepository;
import com.blueteam.historyEdu.repositories.IVideoRepository;
import com.blueteam.historyEdu.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LessonService implements ILessonService {

    private final ILessonRepository lessonRepository;
    private final IQuizRepository quizRepository;
    private final IVideoRepository videoRepository;
    private final IInformationRepository informationRepository;

    @Override
    @Transactional
    public void deleteQuizAndUpdateStt(Long lessonId, Long quizId) throws DataNotFoundException {
        // Lấy quiz cần xóa theo ID
        Optional<Quiz> quiz = quizRepository.findById(quizId);

        if (quiz.isPresent()) {
            Quiz quizEntity = quiz.get();
            Lesson lesson = quizEntity.getLesson();
            lesson.getQuizzes().remove(quizEntity);
            quizRepository.delete(quizEntity);
            quizRepository.flush();
            System.out.println("Quiz deleted: " + quizEntity.getId());



        // Lấy danh sách video, information, và quiz theo lessonId và sắp xếp theo stt
        List<Video> videos = videoRepository.findAllByLessonIdOrderBySttAsc(lessonId);
        List<Information> infos = informationRepository.findAllByLessonIdOrderBySttAsc(lessonId);
        List<Quiz> quizzes = quizRepository.findAllByLessonIdOrderBySttAsc(lessonId);

        // Kết hợp tất cả các danh sách vào một danh sách chung
        List<ItemWithStt> allItems = new ArrayList<>();
        allItems.addAll(videos);
        allItems.addAll(infos);
        allItems.addAll(quizzes);

        // Sắp xếp lại stt sau khi xóa
        for (int i = 0; i < allItems.size(); i++) {
            allItems.get(i).setStt(i + 1); // Đặt lại stt
        }

        // Lưu lại các thay đổi
        videoRepository.saveAll(videos);
        informationRepository.saveAll(infos);
        quizRepository.saveAll(quizzes);
        } else {
            throw new DataNotFoundException(MessageKeys.VIDEO_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public void deleteVideoAndUpdateStt(Long lessonId, Long videoId) throws DataNotFoundException {
        // Lấy video cần xóa theo ID
        Optional<Video> video = videoRepository.findById(videoId);

        if (video.isPresent()) {
            Video videoEntity = video.get();
            Lesson lesson = videoEntity.getLesson();
            lesson.getVideos().remove(videoEntity);
            videoRepository.delete(videoEntity);
            videoRepository.flush();
            System.out.println("Video deleted: " + videoEntity.getId());



        // Lấy danh sách video, information, và quiz theo lessonId và sắp xếp theo stt
        List<Video> videos = videoRepository.findAllByLessonIdOrderBySttAsc(lessonId);
        List<Information> infos = informationRepository.findAllByLessonIdOrderBySttAsc(lessonId);
        List<Quiz> quizzes = quizRepository.findAllByLessonIdOrderBySttAsc(lessonId);

        // Kết hợp tất cả các danh sách vào một danh sách chung
        List<ItemWithStt> allItems = new ArrayList<>();
        allItems.addAll(videos);
        allItems.addAll(infos);
        allItems.addAll(quizzes);

        // Sắp xếp lại stt sau khi xóa
        for (int i = 0; i < allItems.size(); i++) {
            allItems.get(i).setStt(i + 1); // Đặt lại stt
        }

        // Lưu lại các thay đổi
        videoRepository.saveAll(videos);
        informationRepository.saveAll(infos);
        quizRepository.saveAll(quizzes);
        } else {
            throw new DataNotFoundException(MessageKeys.VIDEO_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public void deleteInfoAndUpdateStt(Long lessonId, Long infoId) throws DataNotFoundException {
        // Lấy information cần xóa theo ID
        Optional<Information> information = informationRepository.findById(infoId);
        if (information.isPresent()) {
        Information infoEntity = information.get();
        Lesson lesson = infoEntity.getLesson();
        lesson.getInformations().remove(infoEntity);
        informationRepository.delete(infoEntity);
        informationRepository.flush();
        System.out.println("Information deleted: " + infoEntity.getId());


        // Lấy danh sách video, information, và quiz theo lessonId và sắp xếp theo stt
        List<Video> videos = videoRepository.findAllByLessonIdOrderBySttAsc(lessonId);
        List<Information> infos = informationRepository.findAllByLessonIdOrderBySttAsc(lessonId);
        List<Quiz> quizzes = quizRepository.findAllByLessonIdOrderBySttAsc(lessonId);

        // Kết hợp tất cả các danh sách vào một danh sách chung
        List<ItemWithStt> allItems = new ArrayList<>();
        allItems.addAll(videos);
        allItems.addAll(infos);
        allItems.addAll(quizzes);

        // Sắp xếp lại stt sau khi xóa
        for (int i = 0; i < allItems.size(); i++) {
            allItems.get(i).setStt(i + 1); // Đặt lại stt
        }

        // Lưu lại các thay đổi
        videoRepository.saveAll(videos);
        informationRepository.saveAll(infos);
        quizRepository.saveAll(quizzes);
        }
        else {
            throw new DataNotFoundException(MessageKeys.INFORMATION_NOT_FOUND);
        }
    }
}
