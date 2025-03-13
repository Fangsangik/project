package com.example.project.global.util;

import com.example.project.global.auth.CustomUserDetails;
import com.example.project.global.exception.CustomException;
import com.example.project.global.exception.type.UserErrorCode;
import com.example.project.user.domain.User;
import org.springframework.security.core.userdetails.UserDetails;

public class UserInfo {

    public static User getUser(UserDetails userDetails) {
        if (userDetails instanceof CustomUserDetails) {
            return ((CustomUserDetails) userDetails).getUser();
        }
        throw new CustomException(UserErrorCode.ACCESS_DENIED);
    }
}
