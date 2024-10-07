package com.blueteam.historyEdu.controllers;


import com.blueteam.historyEdu.dtos.InformationDTO;
import com.blueteam.historyEdu.responses.CourseResponse;
import com.blueteam.historyEdu.responses.ResponseObject;
import com.blueteam.historyEdu.services.information.IInformationService;
import com.blueteam.historyEdu.services.lesson.ILessonService;
import com.blueteam.historyEdu.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/infos")
@RequiredArgsConstructor
public class InformationController {

    private final IInformationService informationService;
    private final ILessonService lessonService;

    // api create information
    @PostMapping("/create/{lessonId}")
    public ResponseEntity<ResponseObject> createInformation(@PathVariable Long lessonId, @RequestBody InformationDTO informationDTO) {
        try {
            CourseResponse courseResponse = informationService.createInformation(lessonId, informationDTO);
            return ResponseEntity.status(HttpStatus.OK).body(
                    ResponseObject.builder()
                            .data(courseResponse)
                            .message(MessageKeys.INFORMATION_CREATED_SUCCESSFULLY)
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

    // api update information
    @PutMapping("/update/{informationId}")
    public ResponseEntity<ResponseObject> updateInformation(@PathVariable Long informationId, @RequestBody InformationDTO informationDTO) {
        try {
            CourseResponse courseResponse = informationService.updateInformation(informationId, informationDTO);
            return ResponseEntity.status(HttpStatus.OK).body(
                    ResponseObject.builder()
                            .data(courseResponse)
                            .message(MessageKeys.INFORMATION_UPDATED_SUCCESSFULLY)
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

    // api delete information
    @DeleteMapping("/delete/{lessonId}/{informationId}")
    public ResponseEntity<ResponseObject> deleteInformation(@PathVariable Long lessonId, @PathVariable Long informationId) {
        try {
            lessonService.deleteInfoAndUpdateStt(lessonId, informationId);
            return ResponseEntity.status(HttpStatus.OK).body(
                    ResponseObject.builder()
                            .data(null)
                            .message(MessageKeys.INFORMATION_DELETED_SUCCESSFULLY)
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
