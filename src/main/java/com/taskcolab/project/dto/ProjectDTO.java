package com.taskcolab.project.dto;

import java.util.List;



import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class ProjectDTO {
    private Long id;

    // @NotBlank(message = "Name is mandatory")
    private String name;

    private String description;

    private Long ownerId;

    private boolean archived;

    private List<ProjectMemberDTO> members;
}
