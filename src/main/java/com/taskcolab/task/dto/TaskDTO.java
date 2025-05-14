package com.taskcolab.task.dto;

import com.taskcolab.project.dto.ProjectDTO;
import com.taskcolab.user.dto.UserDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class TaskDTO {
    private Long id;

    @NotBlank(message = "Title is mandatory")
    private String title;

    private String description;

    @NotBlank(message = "Status is mandatory")
    private String status;

    private ProjectDTO project;

    private UserDTO assignee;
}
