package com.blueteam.historyEdu.controllers;


import com.blueteam.historyEdu.dtos.ChangePasswordDTO;
import com.blueteam.historyEdu.dtos.ForgotPasswordDTO;
import com.blueteam.historyEdu.responses.ResponseObject;
import com.blueteam.historyEdu.services.forgotPassword.IForgotPasswordService;
import com.blueteam.historyEdu.services.user.IUserService;
import com.blueteam.historyEdu.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/forgot-password")
@RequiredArgsConstructor
public class ForgotPasswordController {

    private final IForgotPasswordService forgotPasswordService;
    private final IUserService userService;

    @PostMapping("/send-otp/{email}")
    public ResponseEntity<ResponseObject> sendOtp(@PathVariable String email) {
        try {
            forgotPasswordService.verifyEmailAndSendOtp(email);
            return ResponseEntity.status(HttpStatus.OK).body(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message(MessageKeys.OTP_SENT_SUCCESSFULLY)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PostMapping("/verify-otp/{email}")
    public ResponseEntity<ResponseObject> verifyOtp(@PathVariable String email,
                                                    @RequestBody ForgotPasswordDTO forgotPasswordDTO) {
        try {
            forgotPasswordService.verifyOTP(email, forgotPasswordDTO.getOtp());
            return ResponseEntity.status(HttpStatus.OK).body(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message(MessageKeys.OTP_VERIFIED_SUCCESSFULLY)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PostMapping("/change-password/{email}")
    public ResponseEntity<ResponseObject> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO,
                                                         @PathVariable String email) {
        try {
            if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message(MessageKeys.PASSWORD_NOT_MATCH)
                        .build());
            }
            userService.updatePassword(email, changePasswordDTO.getNewPassword());
            return ResponseEntity.status(HttpStatus.OK).body(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message(MessageKeys.CHANGE_PASSWORD_SUCCESSFULLY)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build());
        }
    }
}
