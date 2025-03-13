package com.example.project.global.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UrlConst {

    public static final String ADMIN_INTERCEPTOR_PATH = "/admin/**";

    public static final String[] WHITE_LIST ={"/", "/users", "/admin/**", "/users/login", "/v3/api-docs/**", "/v3/api-docs.yaml", "/swagger-ui/**", "/swagger-ui.html", "/swagger-resources/**", "/webjars/**"};
}
