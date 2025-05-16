package com.taskcolab.project.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taskcolab.common.ApiResponse;
import com.taskcolab.project.dto.ProjectDTO;
import com.taskcolab.project.dto.ProjectRequest;
import com.taskcolab.project.dto.ProjectStatsDTO;
import com.taskcolab.project.service.ProjectService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    
    private final ProjectService projectService;
    private final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    public ProjectController(ProjectService projectService){
        this.projectService = projectService;
    }
    @PostMapping
    public ResponseEntity<ApiResponse<ProjectDTO>> createProject(@Valid @RequestBody ProjectRequest projectRequest, Authentication authentication){

        ProjectDTO createProject = projectService.createProject(projectRequest, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(createProject, "project cre avec succes"));
    }

    @PostMapping("/{projectId}/members/{userId}")
    public ResponseEntity<ApiResponse<Void>> addMember(@PathVariable Long projectId, @PathVariable Long userId){
        projectService.addMember(projectId, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "membre ajoute avec succes"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectDTO>> getProject(@PathVariable Long id){
        ProjectDTO projectDTO = projectService.getProject(id);
        return ResponseEntity.ok(ApiResponse.success(projectDTO, "projet recuperer avec succes"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> getAllProjects(){
        List<ProjectDTO>  projects = projectService.getAlProjects();
        return ResponseEntity.ok(ApiResponse.success(projects, "liste des projets recupere avec succes"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectDTO>> updateProject(@PathVariable Long id, @RequestBody ProjectDTO projectDTO){
        ProjectDTO project = projectService.updateProject(id,projectDTO);
        return ResponseEntity.ok(ApiResponse.success(project,"projet modifier avec success"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteproject(@PathVariable Long id){
        projectService.deleteProject(id);
        return ResponseEntity.ok(ApiResponse.success(null, "suppression avec success"));
    }

    @GetMapping("/my-projects")
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> getMyProjects(Authentication auth){
        List<ProjectDTO> projects = projectService.getUserProject(auth.getName());
        return ResponseEntity.ok(ApiResponse.success(projects, "recuperation des projet de l'utilisateur connecte avec success"));
    }

    @PutMapping("/{id}/archive")
    public ResponseEntity<ApiResponse<ProjectDTO>> archiveProject(@PathVariable Long id, @RequestParam boolean archive){
        ProjectDTO project = projectService.archiveProject(id, archive);
        return ResponseEntity.ok(ApiResponse.success(project, "project archive avec success"));
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<ApiResponse<ProjectStatsDTO>> getProjectStats(@PathVariable Long id){
        ProjectStatsDTO projectStats = projectService.getProjectStats(id);
        return ResponseEntity.ok(ApiResponse.success(projectStats, "l'etat du projet"));
    }

}
