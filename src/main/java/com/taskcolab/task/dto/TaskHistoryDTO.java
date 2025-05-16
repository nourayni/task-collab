package com.taskcolab.task.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TaskHistoryDTO {
    private Long id;
    private String userEmail;
    private String action;
    private LocalDateTime timestamp;
}
