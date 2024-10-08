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
    private String content;
    private Integer stt;

    public static InformationResponse fromInformation(Information information) {
        return InformationResponse.builder()
                .infoId(information.getId())
                .infoTitle(information.getInfoTitle())
                .content(information.getContent())
                .stt(information.getStt())
                .build();
    }
}
