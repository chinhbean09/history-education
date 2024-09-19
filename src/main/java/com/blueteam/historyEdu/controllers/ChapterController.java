package com.blueteam.historyEdu.controllers;

import com.blueteam.historyEdu.dtos.ChapterDTO;
import com.blueteam.historyEdu.responses.CourseResponse;
import com.blueteam.historyEdu.responses.ResponseObject;
import com.blueteam.historyEdu.services.chapter.IChapterService;
import com.blueteam.historyEdu.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/chapters")
@RequiredArgsConstructor
public class ChapterController {

    private final IChapterService chapterService;

    // API to create chapter
    @PostMapping("/create/{courseId}")
    public ResponseEntity<ResponseObject> createChapter(@PathVariable Long courseId, @RequestBody ChapterDTO chapterDTO) {
        try {
            // Pass courseId to service method
            CourseResponse courseResponse = chapterService.createChapter(courseId, chapterDTO);
            return ResponseEntity.status(HttpStatus.OK).body(
                    ResponseObject.builder()
                            .data(courseResponse)
                            .message(MessageKeys.CHAPTER_CREATED_SUCCESSFULLY)
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

    // API to update chapter
    @PutMapping("/update/{chapterId}")
    public ResponseEntity<ResponseObject> updateChapter(@PathVariable Long chapterId, @RequestBody ChapterDTO chapterDTO) {
        try {
            // Pass chapterId to service method
            CourseResponse courseResponse = chapterService.updateChapter(chapterId, chapterDTO);
            return ResponseEntity.status(HttpStatus.OK).body(
                    ResponseObject.builder()
                            .data(courseResponse)
                            .message(MessageKeys.CHAPTER_UPDATED_SUCCESSFULLY)
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

    // API to delete chapter
    @DeleteMapping("/delete/{chapterId}")
    public ResponseEntity<ResponseObject> deleteChapter(@PathVariable Long chapterId) {
        try {
            chapterService.deleteChapter(chapterId);
            return ResponseEntity.status(HttpStatus.OK).body(
                    ResponseObject.builder()
                            .data(null)
                            .message(MessageKeys.CHAPTER_DELETED_SUCCESSFULLY)
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
