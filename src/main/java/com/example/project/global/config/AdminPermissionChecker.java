package com.example.project.global.config;

import com.example.project.global.exception.CustomException;
import com.example.project.global.exception.type.UserErrorCode;
import com.example.project.user.domain.User;
import com.example.project.user.type.UserRole;
import org.springframework.stereotype.Component;

@Component
public class AdminPermissionChecker {

    public void checkAdmin(User principal) {
        if (principal.getRole() != UserRole.ADMIN) {
            throw new CustomException(UserErrorCode.ACCESS_DENIED);
        }
    }

}
