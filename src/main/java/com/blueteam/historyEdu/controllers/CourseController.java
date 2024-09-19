package com.blueteam.historyEdu.controllers;


import com.blueteam.historyEdu.dtos.CourseDTO;
import com.blueteam.historyEdu.entities.Course;
import com.blueteam.historyEdu.responses.CourseResponse;
import com.blueteam.historyEdu.responses.ResponseObject;
import com.blueteam.historyEdu.services.course.ICourseService;
import com.blueteam.historyEdu.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("${api.prefix}/courses")
@RequiredArgsConstructor
public class CourseController {

    private final ICourseService courseService;

    // api create course
    @PostMapping("/create")
    public ResponseEntity<ResponseObject> createCourse(@RequestBody CourseDTO courseDTO) {
        try {
            CourseResponse courseResponse = courseService.createCourse(courseDTO);
            return ResponseEntity.status(HttpStatus.OK).body(
                    ResponseObject.builder()
                            .data(courseResponse)
                            .message(MessageKeys.COURSE_CREATED_SUCCESSFULLY)
                            .status(HttpStatus.OK)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .data(null)
                            .message(e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    // api update course
    @PutMapping("/update/{courseId}")
    public ResponseEntity<ResponseObject> updateCourse(@PathVariable Long courseId, @RequestBody CourseDTO courseDTO) {
        try {
            CourseResponse courseResponse = courseService.updateCourse(courseId, courseDTO);
            return ResponseEntity.status(HttpStatus.OK).body(
                    ResponseObject.builder()
                            .data(courseResponse)
                            .message(MessageKeys.COURSE_UPDATED_SUCCESSFULLY)
                            .status(HttpStatus.OK)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .data(null)
                            .message(e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    // api get all course
    @GetMapping("/get-all")
    public ResponseEntity<ResponseObject> getAllCourse() {
        try {
            Page<CourseResponse> courseResponses = courseService.getAllCourse(0, 10);
            return ResponseEntity.status(HttpStatus.OK).body(
                    ResponseObject.builder()
                            .data(courseResponses)
                            .message(MessageKeys.COURSE_FETCHED_SUCCESSFULLY)
                            .status(HttpStatus.OK)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .data(null)
                            .message(e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    // api get course by id
    @GetMapping("/getDetail/{courseId}")
    public ResponseEntity<ResponseObject> getCourseById(@PathVariable Long courseId) {
        try {
            CourseResponse courseResponse = courseService.getCourseById(courseId);
            return ResponseEntity.status(HttpStatus.OK).body(
                    ResponseObject.builder()
                            .data(courseResponse)
                            .message(MessageKeys.COURSE_FETCHED_SUCCESSFULLY)
                            .status(HttpStatus.OK)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .data(null)
                            .message(e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    // delete course
    @DeleteMapping("/delete/{courseId}")
    public ResponseEntity<ResponseObject> deleteCourse(@PathVariable Long courseId) {
        try {
            courseService.deleteCourse(courseId);
            return ResponseEntity.status(HttpStatus.OK).body(
                    ResponseObject.builder()
                            .data(null)
                            .message(MessageKeys.COURSE_DELETED_SUCCESSFULLY)
                            .status(HttpStatus.OK)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .data(null)
                            .message(e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @PutMapping("/update-image/{courseId}")
    public ResponseEntity<ResponseObject> updateCourseImage(@PathVariable Long courseId, @RequestParam("image") MultipartFile image) {
        try {
            Course course = courseService.uploadCourseImage(courseId, image);
            return ResponseEntity.status(HttpStatus.OK).body(
                    ResponseObject.builder()
                            .data(CourseResponse.fromCourse(course))
                            .message(MessageKeys.COURSE_IMAGE_UPDATED_SUCCESSFULLY)
                            .status(HttpStatus.OK)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .data(null)
                            .message(e.getMessage())
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .build()
            );
        }
    }
}