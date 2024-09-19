package com.blueteam.historyEdu.services.user;

import com.blueteam.historyEdu.dtos.ChangePasswordDTO;
import com.blueteam.historyEdu.dtos.user.UserDTO;
import com.blueteam.historyEdu.dtos.user.UserLoginDTO;
import com.blueteam.historyEdu.entities.User;
import com.blueteam.historyEdu.exceptions.DataNotFoundException;
import com.blueteam.historyEdu.responses.User.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IUserService {
    User registerUser(UserDTO userDTO) throws Exception;

    String login(UserLoginDTO userLoginDTO) throws Exception;

    public User getUserDetailsFromToken(String token) throws DataNotFoundException;

    void sendMailForRegisterSuccess(String fullName,String email, String password);

    void changePassword(Long id, ChangePasswordDTO changePasswordDTO) throws DataNotFoundException;

    void updatePassword(String email, String password) throws DataNotFoundException;

    void blockOrEnable(Long userId, Boolean active) throws Exception;
    Page<UserResponse> getAllUsers(String keyword, PageRequest pageRequest);

    User getUser(Long id) throws DataNotFoundException;

    void deleteUser(Long userId);

    void updateUser(UserDTO userDTO) throws Exception;

    User getUserDetailsFromRefreshToken(String refreshToken) throws Exception;

    List<UserResponse> getAllUsers(Long roleId);

    User updateUserAvatar(long id, MultipartFile avatar);

}
