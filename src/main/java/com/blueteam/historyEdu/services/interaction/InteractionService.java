package com.blueteam.historyEdu.services.interaction;

import com.blueteam.historyEdu.dtos.InteractionDTO;
import com.blueteam.historyEdu.entities.Interaction;
import com.blueteam.historyEdu.entities.User;
import com.blueteam.historyEdu.entities.Video;
import com.blueteam.historyEdu.repositories.IVideoRepository;
import com.blueteam.historyEdu.repositories.InteractionRepository;
import com.blueteam.historyEdu.responses.InteractionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InteractionService implements IInteractionService{

    private final InteractionRepository interactionRepository;
    private final IVideoRepository videoRepository;


    @Override
    @Transactional
    public String addInteraction(Long videoId, InteractionDTO interactionDTO) {
        // Get the current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // check if video exists
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Video not found"));
        // Create a new interaction and associate the current user and video
        Interaction interaction = interactionDTO.toEntity();
        interaction.setVideo(video);
        interaction.setUser(currentUser);

        // Save the interaction
        interactionRepository.save(interaction);
        return "Comment successfully";
    }

    @Override
    @Transactional
    public List<InteractionResponse> getAllInteraction(Long videoId) {
        return interactionRepository.findAllByVideoId(videoId).stream(
                ).map(InteractionResponse::fromInteraction).toList();
    }
}
