package com.blueteam.historyEdu.services.token;

import com.blueteam.historyEdu.entities.Token;
import com.blueteam.historyEdu.entities.User;
import org.springframework.stereotype.Service;

@Service

public interface ITokenService {
    Token addToken(User user, String token, boolean isMobileDevice);

    Token refreshToken(String refreshToken, User user) throws Exception;

    void deleteToken(String token);
}
