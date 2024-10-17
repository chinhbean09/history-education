package com.blueteam.historyEdu.controllers;

import com.blueteam.historyEdu.dtos.ProgressDTO;
import com.blueteam.historyEdu.services.progress.IProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/progress")
@RequiredArgsConstructor

public class ProgressController {
    private final IProgressService progressService;

    //  danh sách tiến trình của người dùng theo khóa học
    @GetMapping("/user/{userId}/course/{chapterId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CUSTOMER')")
    public ResponseEntity<List<ProgressDTO>> getProgressByUserAndCourse(@PathVariable Long userId, @PathVariable Long chapterId) {
        List<ProgressDTO> progressDTOList = progressService.getProgressByUserAndChapter(userId, chapterId);
        return ResponseEntity.ok(progressDTOList);
    }

    // udpate tiến trình của người dùng cho một chương học cụ thể
    @PutMapping("/user/{userId}/chapter/{chapterId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CUSTOMER')")
    public ResponseEntity<Void> updateProgress(@PathVariable Long userId, @PathVariable Long chapterId, @RequestBody ProgressDTO progressDTO) {
        progressService.updateProgress(userId, chapterId, progressDTO);
        return ResponseEntity.ok().build();
    }
}
