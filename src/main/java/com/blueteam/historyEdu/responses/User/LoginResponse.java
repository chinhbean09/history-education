package com.blueteam.historyEdu.responses.User;

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

    //package detail
    @JsonProperty("package_id")
    private Long packageId;

    //package detail
//    @Enumerated(EnumType.STRING)
//    private PackageStatus status;

    //package detail
    @JsonProperty("package_start_date")
    private LocalDate packageStartDate;

    //package detail
    @JsonProperty("package_end_date")
    private LocalDate packageEndDate;
}
