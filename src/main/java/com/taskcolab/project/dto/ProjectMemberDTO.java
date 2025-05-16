package com.taskcolab.project.dto;

import com.taskcolab.user.dto.UserDTO;

import lombok.Data;
@Data
public class ProjectMemberDTO {
    private Long id;



    private ProjectDTO projectDTO;


    private UserDTO userDTO;


    private String role = "MEMBER"; // MEMBER, ADMIN
}
