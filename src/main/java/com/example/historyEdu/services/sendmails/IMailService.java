package com.example.historyEdu.services.sendmails;

import com.example.historyEdu.dtos.DataMailDTO;
import jakarta.mail.MessagingException;

public interface IMailService {
    void sendHtmlMail(DataMailDTO dataMail, String templateName) throws MessagingException;
}
