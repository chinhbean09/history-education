package com.blueteam.historyEdu.services.course;

import com.blueteam.historyEdu.dtos.CourseDTO;
import com.blueteam.historyEdu.dtos.CreateCourseDTO;
import com.blueteam.historyEdu.entities.Course;
import com.blueteam.historyEdu.entities.Quiz;
import com.blueteam.historyEdu.exceptions.DataNotFoundException;
import com.blueteam.historyEdu.exceptions.PermissionDenyException;
import com.blueteam.historyEdu.responses.CourseResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ICourseService {

    CourseResponse createCourse(CourseDTO courseDTO) throws DataNotFoundException, PermissionDenyException;

    CourseResponse updateCourse(Long courseId,CourseDTO courseDTO) throws DataNotFoundException, PermissionDenyException;

    Page<CourseResponse> getAllCourse(int page, int size) throws DataNotFoundException;

    void deleteCourse(Long courseId) throws DataNotFoundException;

    CourseResponse getCourseById(Long courseId) throws DataNotFoundException;

    Course uploadCourseImage(Long courseId, MultipartFile image) throws DataNotFoundException;

    CourseResponse createFullCourse(CreateCourseDTO createCourseDTO) throws DataNotFoundException, PermissionDenyException;

}
