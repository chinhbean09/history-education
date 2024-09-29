package com.blueteam.historyEdu.dtos;


import com.blueteam.historyEdu.entities.Information;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InformationDTO {
    private String infoTitle;
    private Integer stt;

    public Information toEntity() {
        return Information.builder()
                .infoTitle(infoTitle)
                .stt(stt)
                .build();
    }
}
