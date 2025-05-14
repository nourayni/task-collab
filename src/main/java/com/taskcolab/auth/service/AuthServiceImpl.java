package com.taskcolab.auth.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.taskcolab.auth.config.JwtUtil;
import com.taskcolab.user.dto.UserDTO;
import com.taskcolab.user.entity.User;
import com.taskcolab.user.repository.UserRepository;

@Service @Transactional
public  class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void register(UserDTO userDTO) {
        logger.info("enregistrement de l'utilisateur {}", userDTO.getEmail());
        Optional<User> existingUser = userRepository.findByEmail(userDTO.getEmail());
        if (existingUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }
        User user = User.builder()
                .email(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .name(userDTO.getName())
                .role("Role_USER")
                .build();
        userRepository.save(user);
    }

    @Override
    public Map<String, String> login(String username, String password) {
        logger.info("authentification de l'utilisateur {}", username);
        if (username == null || password == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username and password must not be null");
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
                logger.info("User authenticated successfully");
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                logger.info("email du user: {}",userDetails.getUsername());
                String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername());
                logger.info("access token: {}", accessToken);
                String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

                Map<String, String> tokens = new HashMap<>();
                tokens.put("accessToken", accessToken);
                tokens.put("refreshToken", refreshToken);
                return tokens;

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials", e);
        }
    }

    @Override
    public Map<String, String> refreshToken(String refreshToken) {
        logger.info("refresh token {}", refreshToken);
        if (!jwtUtil.isRefreshToken(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid refresh token");
        }
        String username = jwtUtil.extractUsername(refreshToken);
        if(jwtUtil.validateToken(refreshToken, username)) {
            String newAccessToken = jwtUtil.generateAccessToken(username);
            String newRefreshToken = jwtUtil.generateRefreshToken(username);
            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", newAccessToken);
            tokens.put("refreshToken", newRefreshToken);
            return tokens;
        }else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired refresh token");
        }
    }

    @Override
    public Map<String, String> handleOAuth2User(OidcUser oidcUser) {
        // connection avec OIDC keycloak
        logger.info("connection avec OIDC {}", oidcUser.getEmail());
        String email = oidcUser.getEmail();
        if (email == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }
        User user;
        Optional<User> existingUser = userRepository.findByEmail(email);
        
        if (existingUser.isEmpty()) {
            user = User.builder()
                    .email(email)
                    .name(oidcUser.getFullName())
                    .password(passwordEncoder.encode("oauth2-" + email))
                    .role("Role_USER")
                    .build();
            userRepository.save(user);
        }else {
            logger.info("User already exists, logging in");
            user = existingUser.get();
        }
        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }
    
}
