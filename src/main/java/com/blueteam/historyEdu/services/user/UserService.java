package com.blueteam.historyEdu.services.user;

import com.blueteam.historyEdu.components.JwtTokenUtils;
import com.blueteam.historyEdu.components.LocalizationUtils;
import com.blueteam.historyEdu.dtos.ChangePasswordDTO;
import com.blueteam.historyEdu.dtos.DataMailDTO;
import com.blueteam.historyEdu.dtos.user.UserDTO;
import com.blueteam.historyEdu.dtos.user.UserLoginDTO;
import com.blueteam.historyEdu.entities.Role;
import com.blueteam.historyEdu.entities.Token;
import com.blueteam.historyEdu.entities.User;
import com.blueteam.historyEdu.exceptions.DataNotFoundException;
import com.blueteam.historyEdu.exceptions.InvalidParamException;
import com.blueteam.historyEdu.exceptions.PermissionDenyException;
import com.blueteam.historyEdu.repositories.IRoleRepository;
import com.blueteam.historyEdu.repositories.ITokenRepository;
import com.blueteam.historyEdu.repositories.IUserRepository;
import com.blueteam.historyEdu.responses.User.UserResponse;
import com.blueteam.historyEdu.services.sendmails.MailService;
import com.blueteam.historyEdu.utils.MailTemplate;
import com.blueteam.historyEdu.utils.MessageKeys;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final IUserRepository UserRepository;
    private final LocalizationUtils localizationUtils;
    private final IRoleRepository RoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final AuthenticationManager authenticationManager;
    private final MailService mailService;
    private final ITokenRepository TokenRepository;
    private final Cloudinary cloudinary;

    @Override
    @Transactional
    public User registerUser(UserDTO userDTO) throws Exception {
        String phoneNumber = userDTO.getPhoneNumber();
        if (UserRepository.existsByPhoneNumber(phoneNumber) && userDTO.getPhoneNumber() != null) {
            throw new DataIntegrityViolationException(localizationUtils.getLocalizedMessage(MessageKeys.PHONE_NUMBER_ALREADY_EXISTS));
        }

        String email = userDTO.getEmail();
        if (UserRepository.existsByEmail(email)) {
            throw new DataIntegrityViolationException(localizationUtils.getLocalizedMessage(MessageKeys.EMAIL_ALREADY_EXISTS));
        }

        // Sử dụng roleId mặc định là 2 nếu không được truyền vào
        Long roleId = userDTO.getRoleId() != null ? userDTO.getRoleId() : 2L;
        Role role = RoleRepository.findById(roleId)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.ROLE_DOES_NOT_EXISTS)));

        // Check if the current user has permission to register users with the specified role
        if (role.getRoleName().equalsIgnoreCase("ADMIN")) {
            throw new PermissionDenyException("Not allowed to register for an Admin account");
        }

        User newUser = User.builder()
                .email(userDTO.getEmail())
                .phoneNumber(userDTO.getPhoneNumber())
                .password(userDTO.getPassword())
                .fullName(userDTO.getFullName())
                .active(true)
                .facebookAccountId(userDTO.getFacebookAccountId())
                .googleAccountId(userDTO.getGoogleAccountId())
                .build();
        newUser.setRole(role);

        // Kiểm tra nếu có accountId, không yêu cầu password
        if (userDTO.getFacebookAccountId() == null) {
            String password = userDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            newUser.setPassword(encodedPassword);
        }
        User user = UserRepository.save(newUser);
        // send mail
        sendMailForRegisterSuccess(userDTO.getFullName(), userDTO.getEmail(), userDTO.getPassword());
        return user;
    }
    @Override
    public User getUserDetailsFromToken(String token) throws DataNotFoundException {
        if (jwtTokenUtils.isTokenExpired(token)) {
            throw new DataNotFoundException("Token is expired");
        }

        Map<String, String> identifiers = jwtTokenUtils.extractIdentifier(token);
        if (identifiers == null || (identifiers.get("email") == null && identifiers.get("phoneNumber") == null)) {
            logger.error("Identifier extracted from token is null or empty");
            throw new DataNotFoundException("Identifier not found in token");
        }

        String emailOrPhone = identifiers.get("email") != null ? identifiers.get("email") : identifiers.get("phoneNumber");
        Optional<User> user = UserRepository.findByEmailOrPhoneNumber(emailOrPhone, emailOrPhone);

        return user.orElseThrow(() -> {
            logger.error("User not found for identifier: {}", emailOrPhone);
            return new DataNotFoundException("User not found");
        });
    }

    @Override
    public void sendMailForRegisterSuccess(String fullName, String email, String password) {
        try {
            DataMailDTO dataMail = new DataMailDTO();
            dataMail.setTo(email);
            dataMail.setSubject(MailTemplate.SEND_MAIL_SUBJECT.USER_REGISTER);

            Map<String, Object> props = new HashMap<>();
            props.put("fulName", fullName);
            props.put("email", email);
            props.put("password", password);

            dataMail.setProps(props);

            mailService.sendHtmlMail(dataMail, MailTemplate.SEND_MAIL_TEMPLATE.USER_REGISTER);
        } catch (MessagingException exp) {
            logger.error("Failed to send registration success email", exp);
        }
    }

    @Override
    public void changePassword(Long id, ChangePasswordDTO changePasswordDTO) throws DataNotFoundException {
        User exsistingUser = UserRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.USER_NOT_FOUND));
        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), exsistingUser.getPassword())) {
            throw new DataNotFoundException(MessageKeys.OLD_PASSWORD_WRONG);
        }
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            throw new DataNotFoundException(MessageKeys.CONFIRM_PASSWORD_NOT_MATCH);
        }
        exsistingUser.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        UserRepository.save(exsistingUser);
    }

    @Override
    public void updatePassword(String email, String password) throws DataNotFoundException {
        User user = UserRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.USER_NOT_FOUND));
        user.setPassword(passwordEncoder.encode(password));
        UserRepository.save(user);
    }

    @Override
    public String login(UserLoginDTO userLoginDTO) throws Exception {
        String loginIdentifier = userLoginDTO.getLoginIdentifier();
        try {
            User existingUser = UserRepository.findByEmailOrPhoneNumber(loginIdentifier, loginIdentifier)
                    .orElseThrow(() -> new UsernameNotFoundException(MessageKeys.USER_NOT_FOUND));
            if (!passwordEncoder.matches(userLoginDTO.getPassword(), existingUser.getPassword())) {
                throw new BadCredentialsException(MessageKeys.PASSWORD_NOT_MATCH);
            }

            if (!existingUser.isActive()) {
                throw new LockedException(MessageKeys.USER_IS_LOCKED);
            }

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginIdentifier, userLoginDTO.getPassword(), existingUser.getAuthorities());

            authenticationManager.authenticate(authenticationToken);
            return jwtTokenUtils.generateToken(existingUser);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new DataIntegrityViolationException("Multiple users found with the same identifier: " + loginIdentifier);
        } catch (AuthenticationException e) {
            logger.warn("Authentication failed for user: {}", loginIdentifier, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void blockOrEnable(Long userId, Boolean active) throws DataNotFoundException, PermissionDenyException {
        User existingUser = UserRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.USER_NOT_FOUND));
        existingUser.setActive(active);

        if(existingUser.getRole().getRoleName().equals(Role.ADMIN)) {
            throw new PermissionDenyException("Not allowed to block Admin account");
        }
        UserRepository.save(existingUser);
        }
    @Override
    public Page<UserResponse> getAllUsers(String keyword, PageRequest pageRequest) {
        Page<User> usersPage;
        usersPage = UserRepository.searchUsers(keyword, pageRequest);
        return usersPage.map(UserResponse::fromUser);
    }

    @Override
    public User getUser(Long id) throws DataNotFoundException {
        return UserRepository.findById(id).orElseThrow(() -> new DataNotFoundException("User not found"));
    }

    @Override
    public void deleteUser(Long userId) {
        Optional<User> optionalUser = UserRepository.findById(userId);
        List<Token> tokens = TokenRepository.findByUserId(userId);
        TokenRepository.deleteAll(tokens);
        optionalUser.ifPresent(UserRepository::delete);
    }
    @Override
    public void updateUser(UserDTO userDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        if(userDTO.getEmail() != null){
            currentUser.setEmail(userDTO.getEmail());
        }
        if(userDTO.getPhoneNumber() != null) {
            currentUser.setPhoneNumber(userDTO.getPhoneNumber());
        }
        if(userDTO.getFullName() != null ){
            currentUser.setFullName(userDTO.getFullName());

        }
        if(userDTO.getAddress() != null){
            currentUser.setAddress(userDTO.getAddress());
        }
        if(userDTO.getDateOfBirth() != null)    {
            currentUser.setDateOfBirth(userDTO.getDateOfBirth());

        }
        if(userDTO.getGender() != null) {
            currentUser.setGender(userDTO.getGender());
        }
        UserRepository.save(currentUser);
    }

    @Override
    public User getUserDetailsFromRefreshToken(String refreshToken) throws Exception {
        Token existingToken = TokenRepository.findByRefreshToken(refreshToken);
        return getUserDetailsFromToken(existingToken.getToken());
    }

    @Override
    public List<UserResponse> getAllUsers(Long roleId) {
        List<User> users = UserRepository.findByRoleId(roleId);
        return users.stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
    }

    @Override
    public User updateUserAvatar(long id, MultipartFile avatar) {
        User user = UserRepository.findById(id).orElse(null);
        if (user != null && avatar != null && !avatar.isEmpty()) {
            try {
                // Check if the uploaded file is an image
                MediaType mediaType = MediaType.parseMediaType(Objects.requireNonNull(avatar.getContentType()));
                if (!mediaType.isCompatibleWith(MediaType.IMAGE_JPEG) &&
                        !mediaType.isCompatibleWith(MediaType.IMAGE_PNG)) {
                    throw new InvalidParamException(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_MUST_BE_IMAGE));
                }

                // Upload the avatar to Cloudinary
                Map uploadResult = cloudinary.uploader().upload(avatar.getBytes(),
                        ObjectUtils.asMap("folder", "user_avatar/" + id, "public_id", avatar.getOriginalFilename()));

                // Get the URL of the uploaded avatar
                String avatarUrl = uploadResult.get("secure_url").toString();
                user.setAvatar(avatarUrl);

                // Save the updated user entity
                UserRepository.save(user);
                return user;
            } catch (IOException e) {
                logger.error("Failed to upload avatar for user with ID {}", id, e);
            }
        }
        return null;
    }
}
