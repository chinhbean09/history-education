package com.blueteam.historyEdu.services.chapter;

import com.blueteam.historyEdu.dtos.ChapterDTO;
import com.blueteam.historyEdu.entities.Chapter;
import com.blueteam.historyEdu.entities.Course;
import com.blueteam.historyEdu.entities.User;
import com.blueteam.historyEdu.exceptions.DataNotFoundException;
import com.blueteam.historyEdu.exceptions.PermissionDenyException;
import com.blueteam.historyEdu.repositories.IChapterRepository;
import com.blueteam.historyEdu.repositories.ICourseRepository;
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
public class ChapterService implements IChapterService {

    private final IChapterRepository chapterRepository;
    private final ICourseRepository courseRepository;

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
            return CourseResponse.fromCourse(course); // Return updated course response
        } else {
            throw new PermissionDenyException(MessageKeys.PERMISSION_DENIED);
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
            // Save the updated chapter
            chapterRepository.save(chapter);
            return CourseResponse.fromCourse(chapter.getCourse());
        } else {
            throw new PermissionDenyException(MessageKeys.PERMISSION_DENIED);
        }
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
}
