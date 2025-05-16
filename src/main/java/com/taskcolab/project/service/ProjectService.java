package com.taskcolab.project.service;

import java.util.List;

import com.taskcolab.project.dto.ProjectDTO;
import com.taskcolab.project.dto.ProjectRequest;
import com.taskcolab.project.dto.ProjectStatsDTO;

public interface ProjectService {
    ProjectDTO createProject(ProjectRequest projectRequest, String userEmail);
    ProjectDTO getProject(Long id);
    List<ProjectDTO> getAlProjects();
    ProjectDTO updateProject(Long id, ProjectDTO projectDTO);
    void deleteProject(Long id);
    void addMember(Long projectId, Long userId);
    List<ProjectDTO> getUserProject(String email);
    ProjectDTO archiveProject(Long id, boolean archive);
    ProjectStatsDTO getProjectStats(Long id);
}
