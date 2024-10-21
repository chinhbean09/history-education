package com.blueteam.historyEdu.services.chapter;

import com.blueteam.historyEdu.dtos.*;
import com.blueteam.historyEdu.dtos.quiz.QuizDTO;
import com.blueteam.historyEdu.entities.*;
import com.blueteam.historyEdu.exceptions.DataNotFoundException;
import com.blueteam.historyEdu.exceptions.PermissionDenyException;
import com.blueteam.historyEdu.repositories.IChapterRepository;
import com.blueteam.historyEdu.repositories.ICourseRepository;
import com.blueteam.historyEdu.repositories.ILessonRepository;
import com.blueteam.historyEdu.repositories.ProgressRepository;
import com.blueteam.historyEdu.responses.ChapterResponse;
import com.blueteam.historyEdu.responses.CourseResponse;
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
public class ChapterService implements IChapterService {

    private final IChapterRepository chapterRepository;
    private final ICourseRepository courseRepository;
    private final ILessonRepository lessonRepository;
    private final ProgressRepository progressRepository;

    @Override
    @Transactional
    public CourseResponse createChapter(Long courseId, ChapterDTO chapterDTO) throws DataNotFoundException, PermissionDenyException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        if (currentUser.getRole().getRoleName().equals("ADMIN")) {
            // Fetch the course
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new DataNotFoundException(MessageKeys.COURSE_NOT_FOUND));

            // Create chapter and link it to the course
            Chapter chapter = chapterDTO.toEntity(course);
            chapter.setCourse(course); // Set the course reference in the chapter

            // Save the new chapter
            chapterRepository.save(chapter);

            // Add the chapter to the course's chapter list
            course.getChapters().add(chapter);

            // Increment totalChapter by 1
            course.setTotalChapter(course.getTotalChapter() + 1);

            // Create a new lesson linked to this chapter
            Lesson lesson = Lesson.builder()
                    .chapter(chapter)
                    .build();
            lessonRepository.save(lesson);

            // Add the lesson to the chapter's lesson list
            chapter.getLessons().add(lesson);

            // Increment totalLessons by 1
            course.setTotalLessons(course.getTotalLessons() + 1);

            // Save the updated course with new totalChapter and totalLessons
            courseRepository.save(course);

            // Update enrolled users' progress for the new chapter
            updateUserProgressForNewChapter(course, chapter);

            // Return the updated course response
            return CourseResponse.fromCourse(course);
        } else {
            throw new PermissionDenyException(MessageKeys.PERMISSION_DENIED);
        }
    }

    private void updateUserProgressForNewChapter(Course course, Chapter chapter) {
        // Fetch all users enrolled in the course
        List<Progress> enrolledUsersProgress = progressRepository.findByCourse(course);

        for (Progress progress : enrolledUsersProgress) {
            // Check if progress for this chapter already exists
            boolean progressExists = progressRepository.existsByUserAndChapterId(progress.getUser(), chapter.getId());
            if (!progressExists) {
                // Create a new Progress entry for the new chapter
                Progress newProgress = Progress.builder()
                        .user(progress.getUser()) // Link it to the same user
                        .chapterId(chapter.getId()) // Set the new chapter ID
                        .course(course) // Set the course reference
                        .isChapterCompleted(false) // Initially marked as not completed
                        .build();

                // Save the new progress for the chapter
                progressRepository.save(newProgress);
            }
        }
    }


    @Override
    @Transactional
    public CourseResponse updateChapter(Long chapterId, ChapterDTO chapterDTO) throws DataNotFoundException, PermissionDenyException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        if (currentUser.getRole().getRoleName().equals("ADMIN")) {
            // Fetch the chapter
            Chapter chapter = chapterRepository.findById(chapterId)
                    .orElseThrow(() -> new DataNotFoundException(MessageKeys.CHAPTER_NOT_FOUND));
            // Update the chapter
            chapter.setChapterName(chapterDTO.getChapterName());
            chapter.setStt(chapterDTO.getStt());
            // Save the updated chapter
            chapterRepository.save(chapter);
            return CourseResponse.fromCourse(chapter.getCourse());
        } else {
            throw new PermissionDenyException(MessageKeys.PERMISSION_DENIED);
        }
    }
    @Override
    @Transactional
    public CourseResponse updateFullChapter(Long chapterId, ChapterDTO chapterDTO)
            throws DataNotFoundException, PermissionDenyException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        if (!currentUser.getRole().getRoleName().equals("ADMIN")) {
            throw new PermissionDenyException(MessageKeys.PERMISSION_DENIED);
        }

        // Fetch the chapter
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.CHAPTER_NOT_FOUND));

        // Update the basic fields of the chapter
        chapter.setChapterName(chapterDTO.getChapterName());
        chapter.setDescription(chapterDTO.getDescription());
        chapter.setUrl(chapterDTO.getUrl());
        chapter.setStt(chapterDTO.getStt());

        // Update the lessons associated with this chapter
        List<Lesson> existingLessons = chapter.getLessons();
        List<LessonDTO> updatedLessons = chapterDTO.getLessons();

        // Remove lessons that are no longer in the DTO
        existingLessons.removeIf(existingLesson ->
                updatedLessons.stream().noneMatch(dto -> dto.getId().equals(existingLesson.getId())));

        // Add or update lessons from DTO
        for (LessonDTO lessonDTO : updatedLessons) {
            Lesson lesson = existingLessons.stream()
                    .filter(existingLesson -> existingLesson.getId().equals(lessonDTO.getId()))
                    .findFirst()
                    .orElse(new Lesson());
            lesson.setChapter(chapter);

            // Update Videos
            lesson.getVideos().clear();
            for (VideoDTO videoDTO : lessonDTO.getVideos()) {
                lesson.getVideos().add(videoDTO.toEntity());
            }

            // Update Informations
            lesson.getInformations().clear();
            for (InformationDTO infoDTO : lessonDTO.getInformations()) {
                lesson.getInformations().add(infoDTO.toEntity());
            }

            // Update Quizzes
            lesson.getQuizzes().clear(); // Clear existing quizzes for this lesson

            for (QuizDTO quizDTO : lessonDTO.getQuizzes()) {
                // Convert QuizDTO to Quiz entity
                Quiz quiz = quizDTO.toEntity();
                quiz.setLesson(lesson); // Set the lesson reference for the quiz

                // Clear existing questions for this quiz (if any)
                quiz.getQuestions().clear();

                // Add questions to the quiz
                for (QuestionDTO questionDTO : quizDTO.getQuestions()) {
                    quiz.getQuestions().add(questionDTO.toEntity());
                }

                // Add quiz to the lesson
                lesson.getQuizzes().add(quiz);
            }


            if (lesson.getId() == null) { // New lesson
                existingLessons.add(lesson);
            }
        }


        // Save the updated chapter
        chapterRepository.save(chapter);

        return CourseResponse.fromCourse(chapter.getCourse());
    }



    @Override
    @Transactional
    public void deleteChapter(Long chapterId) throws DataNotFoundException {
        Optional<Chapter> chapter = chapterRepository.findById(chapterId);

        if (chapter.isPresent()) {
            Chapter chapterEntity = chapter.get();
            // Remove the chapter from the course's chapter list if necessary
            Course course = chapterEntity.getCourse();
            course.getChapters().remove(chapterEntity);  // Optional: update the course's list
            // Perform delete operation
            chapterRepository.delete(chapterEntity);
            chapterRepository.flush();  // Ensure immediate execution

            System.out.println("Chapter deleted: " + chapterEntity.getId());
        } else {
            throw new DataNotFoundException(MessageKeys.CHAPTER_NOT_FOUND);
        }
    }

    @Override
    public ChapterResponse getChapter(Long chapterId) throws DataNotFoundException {

        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.CHAPTER_NOT_FOUND));
        return ChapterResponse.fromChapter(chapter);
    }
}
