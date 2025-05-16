package com.taskcolab.user.mapper;

import org.springframework.stereotype.Service;

import com.taskcolab.user.dto.UserDTO;
import com.taskcolab.user.entity.User;

@Service
public class UserMapper {
    public UserDTO userToUserDTO(User user){
        return UserDTO.builder()
        .id(user.getId())
        .email(user.getEmail())
        .name(user.getName())
        .role(user.getRole())
        .build();
    }
}
