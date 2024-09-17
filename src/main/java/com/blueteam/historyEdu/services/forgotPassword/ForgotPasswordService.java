package com.blueteam.historyEdu.services.forgotPassword;

import com.blueteam.historyEdu.dtos.DataMailDTO;
import com.blueteam.historyEdu.entities.ForgotPassword;
import com.blueteam.historyEdu.entities.User;
import com.blueteam.historyEdu.exceptions.DataNotFoundException;
import com.blueteam.historyEdu.repositories.IForgotPasswordRepository;
import com.blueteam.historyEdu.repositories.IUserRepository;
import com.blueteam.historyEdu.services.sendmails.MailService;
import com.blueteam.historyEdu.utils.MailTemplate;
import com.blueteam.historyEdu.utils.MessageKeys;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ForgotPasswordService implements IForgotPasswordService {

    private final IForgotPasswordRepository forgotPasswordRepository;
    private final MailService mailService;
    private final IUserRepository userRepository;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordService.class);

    // Method to generate OTP
    private Integer otpGenerator() {
        Random random = new Random();
        return random.nextInt(100_100, 999_999);
    }

    private void sendOtpMail(ForgotPassword forgotPassword) {
        try {
            DataMailDTO dataMailDTO = new DataMailDTO();
            dataMailDTO.setTo(forgotPassword.getUser().getEmail());
            dataMailDTO.setSubject(MailTemplate.SEND_MAIL_SUBJECT.OTP_SEND);

            Map<String, Object> props = new HashMap<>();
            props.put("otp", forgotPassword.getOtp());
            dataMailDTO.setProps(props); // Set props to dataMailDTO

            mailService.sendHtmlMail(dataMailDTO, MailTemplate.SEND_MAIL_TEMPLATE.OTP_SEND_TEMPLATE);
        } catch (MessagingException e) {
            logger.error("Failed to send OTP email", e);
        }
    }

    // Method to delete expired OTP
    private void deleteExpiredOTP(ForgotPassword forgotPassword) {
        if (forgotPassword.getExpirationTime().before(new Date())) {
            forgotPasswordRepository.delete(forgotPassword);
        }
    }

    @Override
    public void verifyEmailAndSendOtp(String email) throws DataNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.USER_NOT_FOUND));
        int otp = otpGenerator();
        ForgotPassword fp = ForgotPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis() + 60 * 1000))
                .verified(false)
                .user(user)
                .build();
        sendOtpMail(fp);
        forgotPasswordRepository.save(fp);
        executorService.schedule(() -> deleteExpiredOTP(fp), 60, TimeUnit.SECONDS);
    }

    @Override
    public void verifyOTP(String email, Integer otp) throws DataNotFoundException {
        ForgotPassword forgotPassword = forgotPasswordRepository.findLatestOtpSent(email)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.OTP_NOT_FOUND));
        if (!forgotPassword.getOtp().equals(otp)) {
            throw new DataNotFoundException(MessageKeys.OTP_INCORRECT);
        }
        if (forgotPassword.getExpirationTime().before(new Date())) {
            throw new DataNotFoundException(MessageKeys.OTP_IS_EXPIRED);
        }
        forgotPassword.setVerified(true);
        forgotPasswordRepository.save(forgotPassword);
    }
}
