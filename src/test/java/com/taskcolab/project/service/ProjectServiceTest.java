package com.taskcolab.project.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taskcolab.project.dto.ProjectDTO;
import com.taskcolab.project.dto.ProjectRequest;
import com.taskcolab.project.dto.ProjectStatsDTO;
import com.taskcolab.project.entity.Project;
import com.taskcolab.project.mapper.ProjectMapper;
import com.taskcolab.project.repository.ProjectRepository;
import com.taskcolab.task.entity.Task;
import com.taskcolab.task.repository.TaskRepository;
import com.taskcolab.user.entity.User;
import com.taskcolab.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    private Logger logger = LoggerFactory.getLogger(ProjectServiceTest.class);

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private User user;
    private Project project;
    private ProjectRequest projectRequest;
    private Task task;
    private ProjectDTO projectDTO;


    @BeforeEach
    void setUp(){
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");

        project = new Project();
        project.setId(1L);
        project.setName("Test project");
        project.setDescription("Description");
        project.setOwner(user);
        project.setArchived(false);

        projectDTO = new ProjectDTO();
        projectDTO.setId(1L);
        projectDTO.setName("Test project");
        projectDTO.setDescription("Description");
        projectDTO.setOwnerId(user.getId());
        projectDTO.setArchived(false);

        projectRequest = new ProjectRequest();
        projectRequest.setName("Test project");
        projectRequest.setDescription("Description");

        task = new Task();
        task.setId(1L);
        task.setStatus("TODO");
        task.setProject(project);

    }

    @Test
    void createProject_success(){

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        when(projectRepository.save(any(Project.class))).thenReturn(project);

        when(projectMapper.mapperProjectToProjectDTO(project)).thenReturn(projectDTO);


        ProjectDTO result = projectService.createProject(projectRequest, "test@test.com");
        logger.info("username: {}",result.getName());

        assertNotNull(result);
        assertEquals("Test project", result.getName());
        assertEquals(1L, result.getOwnerId());
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void getUserProjects_Success(){
        when(projectRepository.findByOwnerEmail("test@test.com")).thenReturn(List.of(project));
        when(projectMapper.mapperProjectToProjectDTO(project)).thenReturn(projectDTO);

        List<ProjectDTO> result = projectService.getUserProject("test@test.com");
        logger.info("message: {}", result.get(0).getName());

        assertEquals(1, result.size());
        assertEquals("Test project", result.get(0).getName());
        verify(projectRepository, times(1)).findByOwnerEmail("test@test.com");
    }

    @Test
    void archiveProject_Success(){
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.mapperProjectToProjectDTO(project)).thenReturn(projectDTO);

        ProjectDTO result = projectService.archiveProject(1L, true);

        assertTrue(!result.isArchived());
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void getProjectStats_Success(){

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(taskRepository.findByProjectId(1L)).thenReturn(List.of(task));

        ProjectStatsDTO result = projectService.getProjectStats(1L);

        logger.info("donnees: {} {} {}",result.getProjectId(),result.getTotalTasks(), result.getTodoTasks());

        assertEquals(1L, result.getProjectId());
        assertEquals(1, result.getTotalTasks());
        assertEquals(1, result.getTodoTasks());
        verify(taskRepository,times(1)).findByProjectId(1L);
    }
    
}
