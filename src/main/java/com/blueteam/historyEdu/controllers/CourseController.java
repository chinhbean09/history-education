package com.blueteam.historyEdu.controllers;


import com.blueteam.historyEdu.dtos.CourseDTO;
import com.blueteam.historyEdu.dtos.CreateCourseDTO;
import com.blueteam.historyEdu.entities.Course;
import com.blueteam.historyEdu.exceptions.DataNotFoundException;
import com.blueteam.historyEdu.responses.CourseResponse;
import com.blueteam.historyEdu.responses.GetAllCourseResponse;
import com.blueteam.historyEdu.responses.ResponseObject;
import com.blueteam.historyEdu.services.course.ICourseService;
import com.blueteam.historyEdu.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/courses")
@RequiredArgsConstructor
public class CourseController {

    private final ICourseService courseService;

    // api create course
    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
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
    public ResponseEntity<ResponseObject> getAllCourse(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) throws DataNotFoundException {
        Page<GetAllCourseResponse> courseResponses = courseService.getAllCourse(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .data(courseResponses)
                        .message(MessageKeys.COURSE_FETCHED_SUCCESSFULLY)
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @GetMapping("/get-all-admin")
    public ResponseEntity<ResponseObject> getAllCourseAdmin(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) throws DataNotFoundException {
        Page<CourseResponse> courseResponses = courseService.getAllCourseAdmin(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .data(courseResponses)
                        .message(MessageKeys.COURSE_FETCHED_SUCCESSFULLY)
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @GetMapping("/get-all-paid-course")
    public ResponseEntity<ResponseObject> getAllPaidCourse() throws DataNotFoundException {
        List<GetAllCourseResponse> courseResponses = courseService.getAllCourseWithPriceGreaterThanZero();
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .data(courseResponses)
                        .message(MessageKeys.COURSE_FETCHED_SUCCESSFULLY)
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @GetMapping("/get-all-free-course")
    public ResponseEntity<ResponseObject> getAllFreeCourse() throws DataNotFoundException {
        List<GetAllCourseResponse> courseResponses = courseService.getAllCourseWithPriceEqualToZero();
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .data(courseResponses)
                        .message(MessageKeys.COURSE_FETCHED_SUCCESSFULLY)
                        .status(HttpStatus.OK)
                        .build()
        );
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

//    @PostMapping("/create-full-course")
//    public ResponseEntity<ResponseObject> createFullCourse(@RequestBody CreateCourseDTO createCourseDTO) {
//        try {
//            CourseResponse courseResponse = courseService.createFullCourse(createCourseDTO);
//            return ResponseEntity.status(HttpStatus.OK).body(
//                    ResponseObject.builder()
//                            .data(courseResponse)
//                            .message(MessageKeys.COURSE_CREATED_SUCCESSFULLY)
//                            .status(HttpStatus.OK)
//                            .build()
//            );
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
//                    ResponseObject.builder()
//                            .data(null)
//                            .message(e.getMessage())
//                            .status(HttpStatus.BAD_REQUEST)
//                            .build()
//            );
//        }
//    }

    @PostMapping(value = "/create-full-course", consumes = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<ResponseObject> createFullCourse(
            @RequestPart("courseDTO") CreateCourseDTO createCourseDTO,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            CourseResponse courseResponse = courseService.createFullCourse(createCourseDTO, image);
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
}