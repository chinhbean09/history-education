package com.blueteam.historyEdu.services.user;

import com.blueteam.historyEdu.dtos.ChangePasswordDTO;
import com.blueteam.historyEdu.dtos.User.UserDTO;
import com.blueteam.historyEdu.dtos.User.UserLoginDTO;
import com.blueteam.historyEdu.entities.User;
import com.blueteam.historyEdu.exceptions.DataNotFoundException;

public interface IUserService {
    User registerUser(UserDTO userDTO) throws Exception;

    String login(UserLoginDTO userLoginDTO) throws Exception;

    public User getUserDetailsFromToken(String token) throws DataNotFoundException;

    void sendMailForRegisterSuccess(String fullName,String email, String password);

    void changePassword(Long id, ChangePasswordDTO changePasswordDTO) throws DataNotFoundException;

    void updatePassword(String email, String password) throws DataNotFoundException;

    }
