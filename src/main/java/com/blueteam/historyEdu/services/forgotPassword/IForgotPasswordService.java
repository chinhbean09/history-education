package com.blueteam.historyEdu.services.forgotPassword;

import com.blueteam.historyEdu.exceptions.DataNotFoundException;

public interface IForgotPasswordService {

    void verifyEmailAndSendOtp(String email) throws DataNotFoundException;

    void verifyOTP(String email, Integer otp) throws DataNotFoundException;
}
