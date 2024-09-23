package com.blueteam.historyEdu.responses;


import com.blueteam.historyEdu.entities.Information;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InformationResponse {

    private Long infoId;
    private String infoTitle;

    public static InformationResponse fromInformation(Information information) {
        return InformationResponse.builder()
                .infoId(information.getId())
                .infoTitle(information.getInfoTitle())
                .build();
    }
}
