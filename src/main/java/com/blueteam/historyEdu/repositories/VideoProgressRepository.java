package com.blueteam.historyEdu.repositories;

import com.blueteam.historyEdu.entities.Progress;
import com.blueteam.historyEdu.entities.Video;
import com.blueteam.historyEdu.entities.VideoProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoProgressRepository extends JpaRepository<VideoProgress, Long> {
    VideoProgress findByProgressAndVideoId(Progress progress, Long videoId);

    Optional<VideoProgress> findByProgressAndVideo(Progress progress, Video video);
}
