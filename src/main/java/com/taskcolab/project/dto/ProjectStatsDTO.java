package com.taskcolab.project.dto;

import lombok.Data;

@Data
public class ProjectStatsDTO {
    private Long projectId;
    private String projectTitle;
    private long totalTasks;
    private long todoTasks;
    private long inProgressTasks;
    private long doneTasks;
}