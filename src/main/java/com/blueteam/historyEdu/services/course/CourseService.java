package com.blueteam.historyEdu.services.course;

import com.blueteam.historyEdu.dtos.ChapterDTO;
import com.blueteam.historyEdu.dtos.CourseDTO;
import com.blueteam.historyEdu.dtos.CreateCourseDTO;
import com.blueteam.historyEdu.entities.Chapter;
import com.blueteam.historyEdu.entities.Course;
import com.blueteam.historyEdu.entities.Lesson;
import com.blueteam.historyEdu.entities.User;
import com.blueteam.historyEdu.exceptions.DataNotFoundException;
import com.blueteam.historyEdu.exceptions.InvalidParamException;
import com.blueteam.historyEdu.exceptions.PermissionDenyException;
import com.blueteam.historyEdu.repositories.IChapterRepository;
import com.blueteam.historyEdu.repositories.ICourseRepository;
import com.blueteam.historyEdu.repositories.ILessonRepository;
import com.blueteam.historyEdu.responses.CourseResponse;
import com.blueteam.historyEdu.responses.GetAllCourseResponse;
import com.blueteam.historyEdu.utils.MessageKeys;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CourseService implements ICourseService {

    private final ICourseRepository courseRepository;
    private final IChapterRepository chapterRepository;
    private final ILessonRepository lessonRepository;
    private final Cloudinary cloudinary;

    @Override
    public CourseResponse createCourse(CourseDTO courseDTO) throws DataNotFoundException, PermissionDenyException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        if (currentUser.getRole().getRoleName().equals("ADMIN")) {
            // check if course exists
            Course existingCourse = courseRepository.findByCourseName(courseDTO.getCourseName());
            if (existingCourse != null) {
                throw new DataNotFoundException(MessageKeys.COURSE_ALREADY_EXISTS);
            }
            Course course = courseDTO.toEntity();
            courseRepository.save(course);
            return CourseResponse.fromCourse(course);
        } else {
            throw new PermissionDenyException(MessageKeys.PERMISSION_DENIED);
        }
    }

    @Override
    @Transactional
    public CourseResponse updateCourse(Long courseId, CourseDTO courseDTO) throws DataNotFoundException, PermissionDenyException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        if (currentUser.getRole().getRoleName().equals("ADMIN")) {
            Course course = courseRepository.findById(courseId).orElseThrow(() -> new DataNotFoundException(MessageKeys.COURSE_NOT_FOUND));
            course.setCourseName(courseDTO.getCourseName());
            course.setDescription(courseDTO.getMoreInformation());
            course.setImage(courseDTO.getImage());
            course.setIntroductionVideoUrl(courseDTO.getIntroductionVideoUrl());
            course.setTotalDuration(courseDTO.getTotalDuration());
            course.setTotalChapter(courseDTO.getTotalChapter());
            course.setTotalVideos(courseDTO.getTotalVideos());
            course.setPrice(courseDTO.getPrice());
            course.setRating(courseDTO.getRating());
            course.setWhatsLearned(courseDTO.getWhatYouWillLearn());
            course.setRequireToPass(courseDTO.getRequireToPass());
            courseRepository.save(course);
            return CourseResponse.fromCourse(course);
        } else {
            throw new PermissionDenyException(MessageKeys.PERMISSION_DENIED);
        }
    }

    @Override
    @Transactional
    public Page<GetAllCourseResponse> getAllCourse(int page, int size) throws DataNotFoundException {
        Pageable pageable = PageRequest.of(page, size);

        Page<Course> courses = courseRepository.findAll(pageable);
        if (courses.isEmpty()) {
            throw new DataNotFoundException(MessageKeys.COURSE_NOT_FOUND);
        }
        return courses.map(GetAllCourseResponse::fromCourse);

    }

    @Override
    @Transactional
    public void deleteCourse(Long courseId) throws DataNotFoundException {
        if (courseRepository.existsById(courseId)) {
            courseRepository.deleteById(courseId);
        } else {
            throw new DataNotFoundException(MessageKeys.COURSE_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public CourseResponse getCourseById(Long courseId) throws DataNotFoundException {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.COURSE_NOT_FOUND));
        return CourseResponse.fromCourse(course);
    }

    @Override
    @Transactional
    public Course uploadCourseImage(Long courseId, MultipartFile image) throws DataNotFoundException {

        Course course = courseRepository.findById(courseId).orElseThrow(() -> new DataNotFoundException(MessageKeys.COURSE_NOT_FOUND));
        if (image != null && !image.isEmpty()) {
            try {
                // Check if the uploaded file is an image
                MediaType mediaType = MediaType.parseMediaType(Objects.requireNonNull(image.getContentType()));
                if (!mediaType.isCompatibleWith(MediaType.IMAGE_JPEG) &&
                        !mediaType.isCompatibleWith(MediaType.IMAGE_PNG)) {
                    throw new InvalidParamException(MessageKeys.UPLOAD_IMAGES_FILE_MUST_BE_IMAGE);
                }

                // Upload the image to Cloudinary
                Map uploadResult = cloudinary.uploader().upload(image.getBytes(),
                        ObjectUtils.asMap("folder", "course_image/" + courseId, "public_id", image.getOriginalFilename()));

                // Get the URL of the uploaded image
                String imageUrl = uploadResult.get("secure_url").toString();
                course.setImage(imageUrl);
                courseRepository.save(course);
                return course;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    @Transactional
    public CourseResponse createFullCourse(CreateCourseDTO createCourseDTO) throws DataNotFoundException, PermissionDenyException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        if (currentUser.getRole().getRoleName().equals("ADMIN")) {
            // Tạo mới course
            Course course = createCourseDTO.toEntity();
            courseRepository.save(course);

            if (course.getChapters() == null) {
                course.setChapters(new ArrayList<>());  // Initialize if null
            }

            // Tạo các chapter nếu có
            if (createCourseDTO.getChapters() != null) {
                for (ChapterDTO chapterDTO : createCourseDTO.getChapters()) {
                    Chapter chapter = chapterDTO.toEntity(course);
                    chapterRepository.save(chapter);
                    course.getChapters().add(chapter);

                    Lesson lesson = Lesson.builder()
                            .chapter(chapter)
                            .build();
                    lessonRepository.save(lesson);
                    chapter.getLessons().add(lesson);
                }
            }

            return CourseResponse.fromCourse(course);
        } else {
            throw new PermissionDenyException(MessageKeys.PERMISSION_DENIED);
        }
    }
}
