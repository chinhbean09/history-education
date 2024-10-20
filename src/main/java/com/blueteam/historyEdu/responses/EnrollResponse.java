package com.blueteam.historyEdu.responses;

import com.blueteam.historyEdu.enums.EnrollStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EnrollResponse {
    private EnrollStatus status;

}
