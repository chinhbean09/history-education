package com.blueteam.historyEdu.responses.User;

import com.blueteam.historyEdu.enums.PackageStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
    @JsonProperty("message")
    private String message;

    @JsonProperty("token")
    private String token;

    @JsonProperty("refresh_token")
    private String refreshToken;

    private String tokenType = "Bearer";
    //user's detail
    private Long id;

    private String fullName;

    private List<String> roles;

    private String avatar;

    private String phoneNumber;

    private String email;

    @Enumerated(EnumType.STRING)
    private PackageStatus packageStatus;

}
