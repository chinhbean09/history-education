package com.blueteam.historyEdu.services.course;

import com.blueteam.historyEdu.dtos.CourseDTO;
import com.blueteam.historyEdu.dtos.CreateCourseDTO;
import com.blueteam.historyEdu.entities.Course;
import com.blueteam.historyEdu.entities.Quiz;
import com.blueteam.historyEdu.enums.EnrollStatus;
import com.blueteam.historyEdu.enums.PackageStatus;
import com.blueteam.historyEdu.exceptions.DataNotFoundException;
import com.blueteam.historyEdu.exceptions.PermissionDenyException;
import com.blueteam.historyEdu.responses.CourseResponse;
import com.blueteam.historyEdu.responses.GetAllCourseResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ICourseService {

    CourseResponse createCourse(CourseDTO courseDTO) throws DataNotFoundException, PermissionDenyException;

    CourseResponse updateCourse(Long courseId,CourseDTO courseDTO) throws DataNotFoundException, PermissionDenyException;

    Page<GetAllCourseResponse> getAllCourse(int page, int size) throws DataNotFoundException;

    Page<CourseResponse> getAllCourseAdmin(int page, int size) throws DataNotFoundException;

    List<GetAllCourseResponse> getAllCourseWithPriceGreaterThanZero() throws DataNotFoundException;

    List<GetAllCourseResponse> getAllCourseWithPriceEqualToZero() throws DataNotFoundException;

    void deleteCourse(Long courseId) throws DataNotFoundException;

    CourseResponse getCourseById(Long courseId, Long userId) throws DataNotFoundException;

    Course uploadCourseImage(Long courseId, MultipartFile image) throws DataNotFoundException;

    CourseResponse createFullCourse(CreateCourseDTO createCourseDTO) throws DataNotFoundException, PermissionDenyException;

    //CourseResponse createFullCourse(CreateCourseDTO createCourseDTO, MultipartFile image) throws DataNotFoundException, PermissionDenyException, IOException;

    EnrollStatus enrollUserInCourse(Long userId, Long courseId);

    String uploadImage(MultipartFile image) throws IOException;

    List<GetAllCourseResponse> searchCourseByName(String name);
}
