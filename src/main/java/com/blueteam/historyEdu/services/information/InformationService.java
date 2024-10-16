package com.blueteam.historyEdu.services.information;

import com.blueteam.historyEdu.dtos.InformationDTO;
import com.blueteam.historyEdu.entities.*;
import com.blueteam.historyEdu.exceptions.DataNotFoundException;
import com.blueteam.historyEdu.exceptions.PermissionDenyException;
import com.blueteam.historyEdu.repositories.IInformationRepository;
import com.blueteam.historyEdu.repositories.ILessonRepository;
import com.blueteam.historyEdu.repositories.InfoProgressRepository;
import com.blueteam.historyEdu.repositories.ProgressRepository;
import com.blueteam.historyEdu.responses.CourseResponse;
import com.blueteam.historyEdu.responses.InformationResponse;
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
public class InformationService implements IInformationService {

    private final IInformationRepository informationRepository;
    private final ILessonRepository lessonRepository;
    private final ProgressRepository progressRepository;
    private final InfoProgressRepository infoProgressRepository;

    @Override
    @Transactional
    public CourseResponse createInformation(Long lessonId, InformationDTO informationDTO) throws DataNotFoundException, PermissionDenyException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        if (currentUser.getRole().getRoleName().equals("ADMIN")) {
            // Fetch the lesson
            Lesson lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new DataNotFoundException(MessageKeys.LESSON_NOT_FOUND));

            // Create and save the new information entity
            Information information = informationDTO.toEntity();
            information.setLesson(lesson);
            informationRepository.save(information);

            // Update enrolled users' progress for the new information
            updateUserProgressForNewInformation(lesson.getChapter().getCourse(), information);

            // Return the updated course response
            return CourseResponse.fromCourse(lesson.getChapter().getCourse());
        } else {
            throw new PermissionDenyException(MessageKeys.PERMISSION_DENIED);
        }
    }

    private void updateUserProgressForNewInformation(Course course, Information information) {
        List<Progress> enrolledUsersProgress = progressRepository.findByCourse(course);
        for (Progress progress : enrolledUsersProgress) {
            InfoProgress infoProgress = InfoProgress.builder()
                    .information(information)
                    .infoId(String.valueOf(information.getId()))
                    .progress(progress)
                    .isViewed(false)
                    .build();
            infoProgressRepository.save(infoProgress);
        }
    }


    @Override
    @Transactional
    public CourseResponse updateInformation(Long informationId, InformationDTO informationDTO) throws DataNotFoundException, PermissionDenyException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        if (currentUser.getRole().getRoleName().equals("ADMIN")) {
            Information information = informationRepository.findById(informationId)
                    .orElseThrow(() -> new DataNotFoundException(MessageKeys.INFORMATION_NOT_FOUND));
            information.setInfoTitle(informationDTO.getInfoTitle());
            information.setContent(informationDTO.getContent());
            information.setStt(informationDTO.getStt());
            informationRepository.save(information);
            return CourseResponse.fromCourse(information.getLesson().getChapter().getCourse());
        } else {
            throw new PermissionDenyException(MessageKeys.PERMISSION_DENIED);
        }
    }

    @Override
    @Transactional
    public void deleteInformation(Long informationId) throws DataNotFoundException {
        Optional<Information> information = informationRepository.findById(informationId);

        if (information.isPresent()) {
            Information infoEntity = information.get();
            Lesson lesson = infoEntity.getLesson();
            lesson.getInformations().remove(infoEntity);
            informationRepository.delete(infoEntity);
            informationRepository.flush();
            System.out.println("Information deleted: " + infoEntity.getId());
        } else {
            throw new DataNotFoundException(MessageKeys.INFORMATION_NOT_FOUND);
        }
    }

    @Override
    public InformationResponse getInformation(Long informationId) throws DataNotFoundException {

        Information information = informationRepository.findById(informationId)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.INFORMATION_NOT_FOUND));
        return InformationResponse.fromInformation(information);
    }
}
