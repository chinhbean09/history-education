package com.blueteam.historyEdu.services.interaction;

import com.blueteam.historyEdu.dtos.InteractionDTO;
import com.blueteam.historyEdu.responses.InteractionResponse;

import java.util.List;

public interface IInteractionService {

    String addInteraction (Long videoId, InteractionDTO interactionDTO);

    List<InteractionResponse> getAllInteraction(Long videoId);
}
