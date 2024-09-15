package com.blueteam.historyEdu.services.sendmails;

import com.blueteam.historyEdu.dtos.DataMailDTO;
import jakarta.mail.MessagingException;

public interface IMailService {
    void sendHtmlMail(DataMailDTO dataMail, String templateName) throws MessagingException;
}
