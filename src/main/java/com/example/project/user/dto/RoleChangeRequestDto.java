package com.example.project.user.dto;

import com.example.project.user.type.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RoleChangeRequestDto {
    private UserRole userRole;

    public RoleChangeRequestDto(UserRole userRole) {
        this.userRole = userRole;
    }
}
