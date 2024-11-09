package com.blueteam.historyEdu.controllers;

import com.blueteam.historyEdu.dtos.InteractionDTO;
import com.blueteam.historyEdu.responses.InteractionResponse;
import com.blueteam.historyEdu.services.interaction.IInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/interactions")
@RequiredArgsConstructor
public class InteractionController {

    private final IInteractionService interactionService;

    // api create interaction
    @PostMapping("/create/{videoId}")
    public String createInteraction(@PathVariable Long videoId, @RequestBody InteractionDTO interactionDTO) {
        return interactionService.addInteraction(videoId, interactionDTO);
    }

    // api get all interaction
    @GetMapping("/get-all/{videoId}")
    public List<InteractionResponse> getAllInteraction(@PathVariable Long videoId) {
        return interactionService.getAllInteraction(videoId);
    }
}
