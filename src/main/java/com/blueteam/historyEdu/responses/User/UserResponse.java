package com.blueteam.historyEdu.responses.User;

import com.blueteam.historyEdu.entities.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

    @NotBlank(message = "email is required")
    private String email;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("address")
    private String address;

    @JsonProperty("is_active")
    private boolean active;

    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

    @JsonProperty("facebook_account_id")
    private String facebookAccountId;

    @JsonProperty("google_account_id")
    private String googleAccountId;

    private String city;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("role")
    private com.blueteam.historyEdu.entities.Role role;

    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .active(user.isActive())
                .dateOfBirth(user.getDateOfBirth())
                .facebookAccountId(user.getFacebookAccountId())
                .googleAccountId(user.getGoogleAccountId())
                .gender(user.getGender())
                .role(user.getRole())
                .avatar(user.getAvatar())
                .build();
    }
}
