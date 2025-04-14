package com.freemarket.freemarket.user.application;

import com.freemarket.freemarket.user.exception.UserException;
import com.freemarket.freemarket.user.api.dto.UserDto;
import com.freemarket.freemarket.user.domain.User;
import com.freemarket.freemarket.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDto.UserResponse getUserProfile(Long userId) {
        User user = getUser(userId);

        return UserDto.UserResponse.from(user);
    }

    @Transactional
    public UserDto.UserResponse updateProfile(Long userId, UserDto.ProfileUpdateRequest request) {
        User user = getUser(userId);
        user.updateProfile(request.name(), request.phone());

        log.info("사용자 프로필 업데이트 완료: {}", userId);

        return UserDto.UserResponse.from(user);
    }

    @Transactional
    public void changePassword(Long userId, UserDto.PasswordChangeRequest request) {
        User user = getUser(userId);

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new UserException.PasswordMismatchException();
        }

        // 새 비밀번호가 현재 비밀번호와 동일한지 확인
        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new UserException.PasswordSameAsOldException();
        }

        // 비밀번호 변경
        user.changePassword(passwordEncoder.encode(request.newPassword()));
        log.info("사용자 비밀번호 변경 완료: {}", userId);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException.UserNotFoundException(userId));
    }
}
