package com.blueteam.historyEdu.services.course;

import com.blueteam.historyEdu.dtos.ChapterDTO;
import com.blueteam.historyEdu.dtos.CourseDTO;
import com.blueteam.historyEdu.dtos.CreateCourseDTO;
import com.blueteam.historyEdu.entities.*;
import com.blueteam.historyEdu.exceptions.DataNotFoundException;
import com.blueteam.historyEdu.exceptions.InvalidParamException;
import com.blueteam.historyEdu.exceptions.PermissionDenyException;
import com.blueteam.historyEdu.repositories.*;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService implements ICourseService {

    private final ICourseRepository courseRepository;
    private final IChapterRepository chapterRepository;
    private final ILessonRepository lessonRepository;
    private final Cloudinary cloudinary;
    private final ProgressRepository progressRepository;
    private final IUserRepository userRepository;
    private final InfoProgressRepository infoProgressRepository;
    private final VideoProgressRepository videoProgressRepository;
    private final QuizProgressRepository quizProgressRepository;

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
            course.setDescription(courseDTO.getDescription());
            course.setMoreInformation(courseDTO.getMoreInformation());
            course.setImage(courseDTO.getImage());
            course.setIntroductionVideoUrl(courseDTO.getIntroductionVideoUrl());
            course.setTotalDuration(courseDTO.getTotalDuration());
            course.setTotalChapter(courseDTO.getTotalChapter());
            course.setTotalLessons(courseDTO.getTotalLessons());
//            course.setPrice(courseDTO.getPrice());
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
//        if (courses.isEmpty()) {
//            throw new DataNotFoundException(MessageKeys.COURSE_NOT_FOUND);
//        }
        return courses.map(GetAllCourseResponse::fromCourse);

    }

    @Override
    public Page<CourseResponse> getAllCourseAdmin(int page, int size) throws DataNotFoundException {
        Pageable pageable = PageRequest.of(page, size);

        Page<Course> courses = courseRepository.findAll(pageable);
//        if (courses.isEmpty()) {
//            throw new DataNotFoundException(MessageKeys.COURSE_NOT_FOUND);
//        }
        return courses.map(CourseResponse::fromCourse);
    }

    @Override
    public List<GetAllCourseResponse> getAllCourseWithPriceGreaterThanZero() throws DataNotFoundException {
        List<Course> courses = courseRepository.findAllByPriceGreaterThan(0);
//        if (courses.isEmpty()) {
//            throw new DataNotFoundException(MessageKeys.COURSE_NOT_FOUND);
//        }
        return courses.stream().map(GetAllCourseResponse::fromCourse).collect(Collectors.toList());
    }

    @Override
    public List<GetAllCourseResponse> getAllCourseWithPriceEqualToZero() throws DataNotFoundException {
        List<Course> courses = courseRepository.findAllByPriceEquals(0);
//        if (courses.isEmpty()) {
//            throw new DataNotFoundException(MessageKeys.COURSE_NOT_FOUND);
//        }
        return courses.stream().map(GetAllCourseResponse::fromCourse).collect(Collectors.toList());
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

            // Initialize totalChapter and totalLessons to 0
            course.setTotalChapter(0L);
            course.setTotalLessons(0L);

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

                    // Increment totalChapter by 1
                    course.setTotalChapter(course.getTotalChapter() + 1);

                    // Tạo các lessons cho mỗi chapter
                    if (chapter.getLessons() == null) {
                        chapter.setLessons(new ArrayList<>());
                    }

                    // Add lessons (You can modify this part to create real lessons as needed)
                    Lesson lesson = Lesson.builder()
                            .chapter(chapter)
                            .build();
                    lessonRepository.save(lesson);
                    chapter.getLessons().add(lesson);

                    // Increment totalLessons by 1 for each lesson
                    course.setTotalLessons(course.getTotalLessons() + 1);
                }
            }

            courseRepository.save(course); // Update course with new totalChapter and totalLessons

            return CourseResponse.fromCourse(course);
        } else {
            throw new PermissionDenyException(MessageKeys.PERMISSION_DENIED);
        }
    }

    @Override
    @Transactional
    public String enrollUserInCourse(Long userId, Long courseId) {
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<Course> courseOptional = courseRepository.findById(courseId);

        if (userOptional.isEmpty() || courseOptional.isEmpty()) {
            return "User or Course not found";
        }

        User user = userOptional.get();
        Course course = courseOptional.get();

        // Check if the user is already enrolled in the course
        Optional<Progress> existingProgress = progressRepository.findByUserAndCourse(user, course);
        if (existingProgress.isPresent()) {
            return "User is already enrolled in this course.";
        }

        // Loop through all chapters in the course
        for (Chapter chapter : course.getChapters()) {
            // Create a new Progress entry for each chapter
            Progress progress = Progress.builder()
                    .user(user)
                    .course(course)
                    .chapterId(chapter.getId()) // Set the current chapter ID
                    .isChapterCompleted(false)
                    .updatedAt(new Date())
                    .build();

            progressRepository.save(progress);

            // Loop through the lessons in the chapter
            for (Lesson lesson : chapter.getLessons()) {
                // Create VideoProgress for each video in the lesson
                for (Video video : lesson.getVideos()) {
                    VideoProgress videoProgress = VideoProgress.builder()
                            .video(video)
                            .progress(progress)  // Link to progress for the current chapter
                            .watchedDuration(0.0) // Initial watched duration
                            .duration(Double.valueOf(video.getDuration())) // Video duration
                            .isCompleted(false)
                            .build();
                    videoProgressRepository.save(videoProgress);
                }

                // Create InfoProgress for each information in the lesson
                for (Information info : lesson.getInformations()) {
                    InfoProgress infoProgress = InfoProgress.builder()
                            .information(info)
                            .infoId(info.getId())
                            .progress(progress)  // Link to progress for the current chapter
                            .isViewed(false)
                            .build();
                    infoProgressRepository.save(infoProgress);
                }

                // Create QuizProgress for each quiz in the lesson
                for (Quiz quiz : lesson.getQuizzes()) {
                    QuizProgress quizProgress = QuizProgress.builder()
                            .quiz(quiz)
                            .progress(progress)  // Link to progress for the current chapter
                            .isCompleted(false)
                            .build();
                    quizProgressRepository.save(quizProgress);
                }
            }
        }

        return "User enrolled in course successfully!";
    }


    @Override
    public String uploadImage(MultipartFile image) throws IOException {
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
                        ObjectUtils.asMap("folder", "course_image/" + UUID.randomUUID(), "public_id", image.getOriginalFilename()));

                // Get the URL of the uploaded image
                return uploadResult.get("secure_url").toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public List<GetAllCourseResponse> searchCourseByName(String name) {
        if (name != null) {
            return courseRepository
                    .findAllByCourseNameContaining(name)
                    .stream()
                    .map(GetAllCourseResponse::fromCourse)
                    .collect(Collectors.toList());
        }
        return null;
    }

//    @Override
//    @Transactional
//    public CourseResponse createFullCourse(CreateCourseDTO createCourseDTO, MultipartFile image) throws DataNotFoundException, PermissionDenyException, IOException {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        User currentUser = (User) authentication.getPrincipal();
//
//        if (currentUser.getRole().getRoleName().equals("ADMIN")) {
//            // Tạo mới course
//            Course course = createCourseDTO.toEntity();
//
//            // Initialize totalChapter and totalLessons to 0
//            course.setTotalChapter(0L);
//            course.setTotalLessons(0L);
//
//            // Upload image nếu có
//            if (image != null && !image.isEmpty()) {
//                // Check if the uploaded file is an image
//                MediaType mediaType = MediaType.parseMediaType(Objects.requireNonNull(image.getContentType()));
//                if (!mediaType.isCompatibleWith(MediaType.IMAGE_JPEG) &&
//                        !mediaType.isCompatibleWith(MediaType.IMAGE_PNG)) {
//                    throw new InvalidParamException(MessageKeys.UPLOAD_IMAGES_FILE_MUST_BE_IMAGE);
//                }
//
//                // Upload the image to Cloudinary
//                Map uploadResult = cloudinary.uploader().upload(image.getBytes(),
//                        ObjectUtils.asMap("folder", "course_image/" + course.getId(), "public_id", image.getOriginalFilename()));
//
//                // Get the URL of the uploaded image
//                String imageUrl = uploadResult.get("secure_url").toString();
//                course.setImage(imageUrl); // Gán URL của ảnh vào course
//            }
//
//            courseRepository.save(course);
//
//            if (course.getChapters() == null) {
//                course.setChapters(new ArrayList<>());  // Initialize if null
//            }
//
//            // Tạo các chapter nếu có
//            if (createCourseDTO.getChapters() != null) {
//                for (ChapterDTO chapterDTO : createCourseDTO.getChapters()) {
//                    Chapter chapter = chapterDTO.toEntity(course);
//                    chapterRepository.save(chapter);
//                    course.getChapters().add(chapter);
//
//                    // Increment totalChapter by 1
//                    course.setTotalChapter(course.getTotalChapter() + 1);
//
//                    // Tạo các lessons cho mỗi chapter
//                    if (chapter.getLessons() == null) {
//                        chapter.setLessons(new ArrayList<>());
//                    }
//
//                    // Add lessons (You can modify this part to create real lessons as needed)
//                    Lesson lesson = Lesson.builder()
//                            .chapter(chapter)
//                            .build();
//                    lessonRepository.save(lesson);
//                    chapter.getLessons().add(lesson);
//
//                    // Increment totalLessons by 1 for each lesson
//                    course.setTotalLessons(course.getTotalLessons() + 1);
//                }
//            }
//
//            courseRepository.save(course); // Update course with new totalChapter and totalLessons
//
//            return CourseResponse.fromCourse(course);
//        } else {
//            throw new PermissionDenyException(MessageKeys.PERMISSION_DENIED);
//        }
//    }

}
