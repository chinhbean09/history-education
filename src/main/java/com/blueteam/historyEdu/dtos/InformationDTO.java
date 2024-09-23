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

    public Information toEntity() {
        return Information.builder()
                .infoTitle(infoTitle)
                .build();
    }
}
