package com.taskcolab.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter @Builder
public class UserDTO {
    private Long id;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotBlank(message = "Name is mandatory")
    private String name;
    
    @NotBlank(message = "Password is mandatory")
    private String password;

    private String role;
}
