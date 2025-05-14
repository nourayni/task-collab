package com.taskcolab.project.dto;

import java.util.List;

import com.taskcolab.user.dto.UserDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class ProjectDTO {
    private Long id;

    @NotBlank(message = "Name is mandatory")
    private String name;

    private String description;

    private UserDTO owner;

    private boolean archived;

    private List<UserDTO> members;
}
