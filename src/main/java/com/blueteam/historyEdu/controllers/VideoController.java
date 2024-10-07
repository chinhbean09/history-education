package com.blueteam.historyEdu.controllers;


import com.blueteam.historyEdu.dtos.VideoDTO;
import com.blueteam.historyEdu.responses.CourseResponse;
import com.blueteam.historyEdu.responses.ResponseObject;
import com.blueteam.historyEdu.services.lesson.ILessonService;
import com.blueteam.historyEdu.services.video.IVideoService;
import com.blueteam.historyEdu.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/videos")
@RequiredArgsConstructor

public class VideoController {

    private final IVideoService videoService;
    private final ILessonService lessonService;

    // api create video
    @PostMapping("/create/{lessonId}")
    public ResponseEntity<ResponseObject> createVideo(@PathVariable Long lessonId, @RequestBody VideoDTO videoDTO) {
        try {
            CourseResponse courseResponse = videoService.createVideo(lessonId, videoDTO);
            return ResponseEntity.status(HttpStatus.OK).body(
                    ResponseObject.builder()
                            .data(courseResponse)
                            .message(MessageKeys.VIDEO_CREATED_SUCCESSFULLY)
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

    // api update video
    @PutMapping("/update/{videoId}")
    public ResponseEntity<ResponseObject> updateVideo(@PathVariable Long videoId, @RequestBody VideoDTO videoDTO) {
        try {
            CourseResponse courseResponse = videoService.updateVideo(videoId, videoDTO);
            return ResponseEntity.status(HttpStatus.OK).body(
                    ResponseObject.builder()
                            .data(courseResponse)
                            .message(MessageKeys.VIDEO_UPDATED_SUCCESSFULLY)
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

    // api delete video
    @DeleteMapping("/delete/{lessonId}/{videoId}")
    public ResponseEntity<ResponseObject> deleteVideo(@PathVariable Long lessonId, @PathVariable Long videoId) {
        try {
            lessonService.deleteVideoAndUpdateStt(lessonId, videoId);
            return ResponseEntity.status(HttpStatus.OK).body(
                    ResponseObject.builder()
                            .data(null)
                            .message(MessageKeys.VIDEO_DELETED_SUCCESSFULLY)
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
