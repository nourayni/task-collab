package com.taskcolab.auth.service;

import java.util.Map;

import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import com.taskcolab.user.dto.UserDTO;

public interface  AuthService {
    void register(UserDTO userDTO);
    Map<String, String> login(String username, String password);
    Map<String, String> refreshToken(String refreshToken);
    Map<String, String> handleOAuth2User(OidcUser oidcUser);
}
